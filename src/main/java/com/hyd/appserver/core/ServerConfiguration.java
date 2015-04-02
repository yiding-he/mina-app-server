package com.hyd.appserver.core;

import com.hyd.appserver.Authenticator;
import com.hyd.appserver.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器设置。当服务器启动后，修改这里的值不会有任何效果。
 *
 * @author yiding.he
 */
public class ServerConfiguration {

    public static final String DEFAULT_LISTEN_IP = "0.0.0.0";

    public static final int DEFAULT_BUFFER_SIZE = 40960;

    public static final int DEFAULT_IDLE_WAIT = 10;

    public static final int DEFAULT_LISTEN_PORT = 8090;

    public static final int DEFAULT_ADMIN_PORT = DEFAULT_LISTEN_PORT + 1000;

    public static final int DEFAULT_MAX_PROCESSORS = Math.min(30, (Runtime.getRuntime().availableProcessors() + 1) * 4);

    public static final boolean DEFAULT_HTTP_TEST_ENABLED = true;

    public static final String DEFAULT_SHUTDOWN_SECRET = System.getProperty("server.shutdownSecret");

    public static final ServerConfiguration DEFAULT_CONFIG = new ServerConfiguration();

    public static final String DEFAULT_ADMIN_USERNAME = "admin";

    public static final String DEFAULT_ADMIN_PASSWORD = "admin";

    /////////////////////////////////////////

    private String ip = DEFAULT_LISTEN_IP;                         // 服务器侦听地址

    private int listenPort = DEFAULT_LISTEN_PORT;                  // 服务器侦听端口

    private int adminListenPort = DEFAULT_ADMIN_PORT;              // 服务器侦听端口

    private int maxProcessorThreads = DEFAULT_MAX_PROCESSORS;      // 最大处理线程数

    private int idleWaitSeconds = DEFAULT_IDLE_WAIT;               // 连接空闲超时时间

    private List<String> ipWhiteList = new ArrayList<String>();         // IP 地址白名单（仅当不为空时有效）

    private int bufferSize = DEFAULT_BUFFER_SIZE;                  // 缓存大小

    private boolean httpTestEnabled = DEFAULT_HTTP_TEST_ENABLED;   // 是否允许浏览器调用接口（否则只能查看文档）

    private String shutdownSecret = DEFAULT_SHUTDOWN_SECRET;       // 关闭服务器的密码

    private String adminUsername = DEFAULT_ADMIN_USERNAME;         // 管理员帐号。只有管理员才能通过命令关闭和重启服务器。

    private String adminPassword = DEFAULT_ADMIN_PASSWORD;         // 管理员密码。只有管理员才能通过命令关闭和重启服务器。

    private Authenticator authenticator = null;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        if (StringUtils.isNotBlank(ip)) {
            this.ip = ip;
        }
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public String getShutdownSecret() {
        return shutdownSecret;
    }

    public void setShutdownSecret(String shutdownSecret) {
        this.shutdownSecret = shutdownSecret;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
        this.adminListenPort = listenPort + 1000;
    }

    public int getMaxProcessorThreads() {
        return maxProcessorThreads;
    }

    public void setMaxProcessorThreads(int maxProcessorThreads) {
        this.maxProcessorThreads = maxProcessorThreads;
    }

    public int getIdleWaitSeconds() {
        return idleWaitSeconds;
    }

    public void setIdleWaitSeconds(int idleWaitSeconds) {
        this.idleWaitSeconds = idleWaitSeconds;
    }

    public List<String> getIpWhiteList() {
        return ipWhiteList;
    }

    public void setIpWhiteList(List<String> ipWhiteList) {
        this.ipWhiteList = ipWhiteList;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean isHttpTestEnabled() {
        return httpTestEnabled;
    }

    public void setHttpTestEnabled(boolean httpTestEnabled) {
        this.httpTestEnabled = httpTestEnabled;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setAdminListenPort(int adminListenPort) {
        this.adminListenPort = adminListenPort;
    }

    public int getAdminListenPort() {
        return adminListenPort;
    }
}
