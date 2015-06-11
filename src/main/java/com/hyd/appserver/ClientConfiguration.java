package com.hyd.appserver;

import com.hyd.appserver.utils.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端配置
 * <p/>
 * 这里没有配置空闲超时时间，是因为这个度量由服务器端来决定
 *
 * @author yiding.he
 */
public class ClientConfiguration {

    /**
     * 服务器地址列表
     */
    private List<InetSocketAddress> serverAddresses = new ArrayList<InetSocketAddress>();

    /**
     * 每个服务器的最大连接数
     */
    private int maxConnectionsPerServer = 10;

    /**
     * 连接池等待超时时间
     */
    private int poolTimeoutSec = 10;

    /**
     * 连接服务器超时时间
     */
    private int socketConnTimeoutSec = 10;

    /**
     * 收发消息超时时间
     */
    private int socketDataTimeoutSec = 10;

    /**
     * 是否当连接池满的时候拒绝所有新的请求（而不是等待）。如果设置为 true，则 {@link #poolTimeoutSec} 将被忽略
     */
    private boolean failWhenPoolExhausted = false;

    public void addServer(String server, int port) {
        this.serverAddresses.add(new InetSocketAddress(server, port));
    }

    /**
     * 删除服务器配置。注意：直接调用本方法是不会真正从连接池中删除服务器的，必须
     * 调用 {@link MinaAppClient#deleteServer(String, int)} 方法。
     *
     * @param server 要删除的服务器地址
     * @param port   要删除的服务器端口号
     */
    protected void deleteServer(String server, int port) {
        synchronized (this.serverAddresses) {
            Iterator<InetSocketAddress> iterator = this.serverAddresses.iterator();
            while (iterator.hasNext()) {
                InetSocketAddress address = iterator.next();
                if (address.getHostName().equals(server) && address.getPort() == port) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public List<InetSocketAddress> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(List<InetSocketAddress> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    /**
     * 设置服务器地址列表
     *
     * @param serverAddresses 服务器地址列表，可包含多个服务器地址，用逗号隔开，每个地址的格式为“server:port”
     */
    public void setServerAddressesString(String serverAddresses) {
        serverAddresses = serverAddresses.replaceAll("\\s", "");
        String[] frags = serverAddresses.split(",");
        List<InetSocketAddress> addrList = new ArrayList<InetSocketAddress>();

        for (String frag : frags) {
            frag = frag.trim();

            if (StringUtils.isEmpty(frag) || !(frag.matches("^[a-zA-Z0-9\\.-]+:\\d+$"))) {
                continue;
            }

            String host = StringUtils.substringBefore(frag, ":");
            int port = Integer.parseInt(StringUtils.substringAfter(frag, ":"));
            addrList.add(new InetSocketAddress(host, port));
        }

        this.serverAddresses = addrList;
    }

    public int getMaxConnectionsPerServer() {
        return maxConnectionsPerServer;
    }

    public void setMaxConnectionsPerServer(int maxConnectionsPerServer) {
        this.maxConnectionsPerServer = maxConnectionsPerServer;
    }

    public int getPoolTimeoutSec() {
        return poolTimeoutSec;
    }

    public void setPoolTimeoutSec(int poolTimeoutSec) {
        this.poolTimeoutSec = poolTimeoutSec;
    }

    public int getSocketConnTimeoutSec() {
        return socketConnTimeoutSec;
    }

    public void setSocketConnTimeoutSec(int socketConnTimeoutSec) {
        this.socketConnTimeoutSec = socketConnTimeoutSec;
    }

    public int getSocketDataTimeoutSec() {
        return socketDataTimeoutSec;
    }

    public void setSocketDataTimeoutSec(int socketDataTimeoutSec) {
        this.socketDataTimeoutSec = socketDataTimeoutSec;
    }

    public boolean isFailWhenPoolExhausted() {
        return failWhenPoolExhausted;
    }

    public void setFailWhenPoolExhausted(boolean failWhenPoolExhausted) {
        this.failWhenPoolExhausted = failWhenPoolExhausted;
    }
}
