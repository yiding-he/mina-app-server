package com.hyd.appserver.core;

import com.hyd.appserver.AppClientException;
import com.hyd.appserver.ClientConfiguration;
import com.hyd.appserver.utils.IoSessionPool;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管理多个连接池，每个连接池对应一个服务器地址。当需要连接时，以轮询的方式从连接池取一个连接。
 *
 * @author yiding.he
 */
public class IoSessionPoolManager {

    private static final Logger LOG = LoggerFactory.getLogger(IoSessionPoolManager.class);

    public static final int CHECK_INTERVAL_MILLIS = 10000;

    private final Map<InetSocketAddress, IoSessionPool> poolMappings = new HashMap<InetSocketAddress, IoSessionPool>();

    private List<IoSessionPool> availablePools = Collections.synchronizedList(new ArrayList<IoSessionPool>());

    private ClientConfiguration configuration;

    private final AtomicInteger roller = new AtomicInteger(0);

    private Timer observerTimer = new Timer("MinaAppServerChecker", true);

    /**
     * 构造方法
     *
     * @param configuration 客户端配置
     */
    public IoSessionPoolManager(ClientConfiguration configuration) {

        this.configuration = configuration;

        // fill availablePools
        for (InetSocketAddress address : configuration.getServerAddresses()) {
            IoSessionPool pool = new IoSessionPool(
                    address,
                    configuration.getMaxConnectionsPerServer(),
                    configuration.getPoolTimeoutSec(),
                    configuration.getSocketConnTimeoutSec(),
                    configuration.getSocketDataTimeoutSec(),
                    configuration.isFailWhenPoolExhausted()
            );

            poolMappings.put(address, pool);

            // 先把地址加入可用服务器列表
            availablePools.add(pool);
        }

        // start checking schedule task
        observerTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                testAllServer();
            }
        }, CHECK_INTERVAL_MILLIS, CHECK_INTERVAL_MILLIS);
    }

    /**
     * 将 IoSession 对象交还给池
     *
     * @param session IoSession 对象
     */
    public void returnIoSession(IoSession session) {
        InetSocketAddress addr = (InetSocketAddress) session.getRemoteAddress();
        IoSessionPool pool = poolMappings.get(addr);

        if (pool != null) {
            pool.returnIoSession(session);
        } else {
            LOG.warn("Leaked IoSession for " + addr);
        }
    }

    /**
     * 获取一个可用连接。如果当前没有可用连接，则抛出异常。
     *
     * @return 可用连接
     */
    public IoSession borrowIoSession() {
        return getIoSession(true);
    }

    /**
     * 获取可用的连接
     *
     * @param retest 当可用服务器列表为空时，是否立刻测试所有服务器。如果为 true，则重新扫描
     *               所有服务器，找到可用的；否则抛出异常
     *
     * @return 连接
     */
    private IoSession getIoSession(boolean retest) {
        IoSessionPool pool;

        if (availablePools.isEmpty()) {

            if (retest) {
                testAllServer();
                return getIoSession(false);
            } else {
                throw new AppClientException("No available connection");
            }

        }

        IoSession ioSession = null;
        boolean success = false;

        while (!availablePools.isEmpty() && !success) {

            if (availablePools.size() == 1) {
                pool = availablePools.get(0);
            } else {
                int index = nextIndex();
                pool = availablePools.get(index);
            }

            try {
                ioSession = pool.borrowIoSession();
                ioSession.setAttribute("pool", pool);
                success = true;
            } catch (AppClientException e) {
                LOG.info("Failed to open session: " + e.getMessage());
                pool.setAvailableStatus(false);
                availablePools.remove(pool);
            }
        }

        if (ioSession == null) {
            throw new AppClientException("No available connection");
        }

        return ioSession;
    }

    private int nextIndex() {
        synchronized (roller) {
            if (roller.intValue() >= availablePools.size() - 1) {
                roller.set(-1);
            }
            return roller.incrementAndGet();
        }
    }

    public void shutdown() {
        observerTimer.cancel();

        for (IoSessionPool pool : poolMappings.values()) {
            try {
                pool.dispose();
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
    }

    public void addServer(String server, int port) {
        InetSocketAddress address = new InetSocketAddress(server, port);
        if (!poolMappings.containsKey(address)) {
            poolMappings.put(address, new IoSessionPool(
                    address,
                    configuration.getMaxConnectionsPerServer(),
                    configuration.getPoolTimeoutSec(),
                    configuration.getSocketConnTimeoutSec(),
                    configuration.getSocketDataTimeoutSec()
            ));
        }
    }

    public void deleteServer(String server, int port) {
        InetSocketAddress address = new InetSocketAddress(server, port);

        synchronized (poolMappings) {
            Iterator<Map.Entry<InetSocketAddress, IoSessionPool>> iterator = this.poolMappings.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<InetSocketAddress, IoSessionPool> entry = iterator.next();
                if (entry.getKey().equals(address)) {
                    disposePool(entry);
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private void testAllServer() {

        for (IoSessionPool pool : poolMappings.values()) {
            pool.test();

            if (pool.isAvailable()) {
                if (!availablePools.contains(pool)) {
                    availablePools.add(pool);
                }
            } else {
                if (availablePools.contains(pool)) {
                    availablePools.remove(pool);
                }
            }
        }
    }

    private void disposePool(Map.Entry<InetSocketAddress, IoSessionPool> entry) {
        try {
            entry.getValue().dispose();
        } catch (Exception e) {
            LOG.error("Error closing pool", e);
        }
    }

    public Map<String, String> getPoolStatus() {
        Map<String, String> result = new HashMap<String, String>();

        for (InetSocketAddress addr : poolMappings.keySet()) {
            IoSessionPool pool = poolMappings.get(addr);
            result.put(addr.toString(), pool.getActiveCount() + "/" + pool.getMaxCount());
        }

        return result;
    }

    public List<String> getAvailablePoolNames() {
        List<String> result = new ArrayList<String>();
        for (IoSessionPool pool : availablePools) {
            result.add(pool.getServerAddress().toString());
        }
        return result;
    }

}
