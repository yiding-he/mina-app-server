package com.hyd.appserver.utils;

import com.hyd.appserver.AppClientException;
import com.hyd.appserver.ServerUnreachableException;
import com.hyd.appserver.json.Constants;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * 客户端连接池。
 * 1、对于连接到任何一个服务器只会有一个连接池。
 * 2、连接池一旦关闭就无法再继续使用。
 *
 * @author yiding.he
 */
public class IoSessionPool {

    private static final Logger LOG = LoggerFactory.getLogger(IoSessionPool.class);

    private static final long DEFAULT_CONNECTION_TIMEOUT = 30L;

    private final InetSocketAddress serverAddress;

    /////////////////////////////////////////

    private NioSocketConnector connector;

    private ObjectPool<IoSession> pool;

    /**
     * 标识本连接池是否可用。IOSessionPoolManager 会每隔一段时间调用 test() 方法检查服务器是否有效。
     * 当服务器失效时，available 将为 false，IOSessionPoolManager 将不会从本连接池获取连接，直到下
     * 次检查成功为止。
     */
    private boolean available = true;

    private int maxCount;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    //////////////// 性能配置 ////////////////

    // 创建连接时的超时时间
    private long connTimeoutSec = DEFAULT_CONNECTION_TIMEOUT;

    public IoSessionPool(InetSocketAddress serverAddress, int poolSize,
                         int poolTimeoutSec, int connTimeoutSec, int dataTimeoutSec) {
        this(serverAddress, poolSize, poolTimeoutSec, connTimeoutSec, dataTimeoutSec, false);
    }


    public IoSessionPool(InetSocketAddress serverAddress, int poolSize,
                         int poolTimeoutSec, int connTimeoutSec, int dataTimeoutSec, boolean failWhenExhausted) {

        this.serverAddress = serverAddress;
        this.connTimeoutSec = connTimeoutSec;
        this.maxCount = poolSize;

        IoSessionFactory factory = new IoSessionFactory();
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setMaxTotal(poolSize);
        config.setTestOnBorrow(true);
        config.setBlockWhenExhausted(!failWhenExhausted);

        if (poolTimeoutSec > 0) {
            config.setMaxWaitMillis(poolTimeoutSec * 1000);
        }

        this.pool = new GenericObjectPool<>(factory, config);

        initConnector(dataTimeoutSec);
    }

    public InetSocketAddress getServerAddress() {
        return this.serverAddress;
    }

    private void initConnector(int dataTimeoutSec) {
        connector = new NioSocketConnector();
        connector.getSessionConfig().setKeepAlive(true);
        connector.getSessionConfig().setUseReadOperation(true);
        connector.getSessionConfig().setReuseAddress(true);
        connector.getSessionConfig().setReadBufferSize(4 * 1024);
        connector.getSessionConfig().setMaxReadBufferSize(100 * 1024 * 1024);
        connector.getSessionConfig().setSendBufferSize(100 * 1024);

        if (dataTimeoutSec > 0) {
            connector.getSessionConfig().setBothIdleTime(dataTimeoutSec);
        }

        connector.getFilterChain().addLast("logger", MinaUtils.createLoggingFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(createTextLineCodecFactory()));
    }

    private TextLineCodecFactory createTextLineCodecFactory() {
        String delimiter = new String(new char[]{Constants.END_SIGN});

        TextLineCodecFactory codecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"), delimiter, delimiter);
        codecFactory.setEncoderMaxLineLength(100 * 1024 * 1024);
        codecFactory.setDecoderMaxLineLength(100 * 1024 * 1024);
        return codecFactory;
    }

    public void setConnTimeoutSec(long connTimeoutSec) {
        this.connTimeoutSec = connTimeoutSec;
    }

    public IoSession borrowIoSession() {
        try {
            return this.pool.borrowObject();
        } catch (AppClientException e) {
            throw e;
        } catch (Exception e) {
            throw new AppClientException(e);
        }
    }

    public void returnIoSession(IoSession session) {
        try {
            this.pool.returnObject(session);
        } catch (AppClientException e) {
            throw e;
        } catch (Exception e) {
            throw new AppClientException(e);
        }
    }

    /**
     * 关闭连接池
     */
    public void dispose() {
        try {
            this.connector.dispose(true);
            this.pool.clear();
            this.pool.close();
        } catch (Exception e) {
            throw new AppClientException(e);
        }
    }

    public int getActiveCount() {
        return pool.getNumActive();
    }

    public int getIdleCount() {
        return pool.getNumIdle();
    }

    public void test() {
        try {
            LOG.debug("testing server [{}]...", this.serverAddress);
            touchServer();
            setAvailableStatus(true);
        } catch (Exception e) {
            setAvailableStatus(false);
        }
    }

    // 尝试连一下服务器，然后断开
    private void touchServer() throws IOException {
        Socket socket = new Socket();
        socket.connect(serverAddress, (int) connTimeoutSec * 1000);
        socket.close();
    }

    public void setAvailableStatus(boolean available) {
        if (available) {
            LOG.debug("server [{}] is available.", this.serverAddress);
        } else {
            LOG.warn("SERVER [{}] IS NOW OFFLINE", this.serverAddress);
        }
        this.available = available;
    }

    /**
     * 创建一个连接
     *
     * @return 连接
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private IoSession createIoSession() {
        ConnectFuture future = connector.connect(serverAddress);
        future.awaitUninterruptibly(connTimeoutSec, TimeUnit.SECONDS);

        IoSession session;
        if (!future.isConnected()) {
            if (future.getException() != null) {
                throw new ServerUnreachableException("无法连接到服务器" + serverAddress + " - " + future.getException());
            } else {
                throw new ServerUnreachableException("无法连接到服务器 " + serverAddress);
            }
        } else {
            session = future.getSession();
        }

        return session;
    }

    public int getMaxCount() {
        return maxCount;
    }


    /////////////////////////////////////////

    private class IoSessionFactory implements PooledObjectFactory<IoSession> {

        @Override
        public PooledObject<IoSession> makeObject() throws Exception {
            LOG.debug("creating session...");
            return new DefaultPooledObject<>(createIoSession());
        }

        @Override
        public void destroyObject(PooledObject<IoSession> p) throws Exception {
            LOG.debug("Closing session...");
            p.getObject().closeNow();
        }

        @Override
        public boolean validateObject(PooledObject<IoSession> p) {
            // 如果闲置状态超过指定时间，则视为无效连接
            IoSession session = p.getObject();
            long idled = System.currentTimeMillis() - session.getLastBothIdleTime();
            return idled < connTimeoutSec * 1000 && session.isConnected() && !session.isClosing();
        }

        @Override
        public void activateObject(PooledObject<IoSession> p) throws Exception {

        }

        @Override
        public void passivateObject(PooledObject<IoSession> p) throws Exception {

        }
    }
}
