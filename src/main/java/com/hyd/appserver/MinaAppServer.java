package com.hyd.appserver;

import com.hyd.appserver.core.ActionFactory;
import com.hyd.appserver.core.AppServerCore;
import com.hyd.appserver.core.IoServiceMappings;
import com.hyd.appserver.core.ServerConfiguration;
import com.hyd.appserver.core.interceptors.AuthticationInterceptor;
import com.hyd.appserver.core.interceptors.DefaultExceptionInterceptor;
import com.hyd.appserver.core.interceptors.HttpTestEnabledInterceptor;
import com.hyd.appserver.filters.IoPerformanceFilter;
import com.hyd.appserver.filters.IpWhiteListFilter;
import com.hyd.appserver.http.NanoHttpdServer;
import com.hyd.appserver.json.*;
import com.hyd.appserver.snapshot.Snapshot;
import com.hyd.appserver.utils.MinaUtils;
import fi.iki.elonen.NanoHTTPD;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.handler.demux.DemuxingIoHandler;
import org.apache.mina.handler.demux.ExceptionHandler;
import org.apache.mina.handler.demux.MessageHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * 应用服务器端。创建一个 MinaAppServer 实例的最好办法是调用
 * {@link com.hyd.appserver.core.AppServerFactory#createServer(java.util.Properties)}
 * 方法。
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class MinaAppServer {

    public static final String VERSION_STRING = "2.0";

    private static final Logger LOG = LoggerFactory.getLogger(MinaAppServer.class);

    static final Logger IO_LOG = LoggerFactory.getLogger("java.io.mina.exceptions");

    private static final List<MinaAppServer> INSTANCES = new ArrayList<>();

    //////////////// 其他成员 ////////////////////

    private boolean started = false;

    private ServerConfiguration configuration;

    private AppServerCore core;

    private NioSocketAcceptor mainAcceptor;

    private NanoHttpdServer nanoHttpdServer;

    private Thread shutdownHookThread = new Thread(this::shutdown);

    private ContextListener contextListener;

    private Snapshot snapshot = new Snapshot();

    /**
     * 其他附加在 MinaAppServer 实例上的属性，例如 Spring 的 ActionContext 就附在上面
     */
    private Map<String, Object> properties = new HashMap<>();

    public MinaAppServer(int port) {
        this(ServerConfiguration.DEFAULT_LISTEN_IP, port, port + 1000, ServerConfiguration.DEFAULT_MAX_PROCESSORS, ServerConfiguration.DEFAULT_IDLE_WAIT);
    }

    public MinaAppServer(int port, int maxProcessorThreads) {
        this(ServerConfiguration.DEFAULT_LISTEN_IP, port, port + 1000, maxProcessorThreads, ServerConfiguration.DEFAULT_IDLE_WAIT);
    }

    /**
     * 构造方法
     *
     * @param port                端口号
     * @param adminPort           http 管理界面端口号
     * @param maxProcessorThreads 最大处理线程数
     * @param idleWaitSeconds     客户端连接空闲超时时间
     */
    public MinaAppServer(String ip, int port, int adminPort, int maxProcessorThreads, int idleWaitSeconds) {
        INSTANCES.add(this);

        this.configuration = new ServerConfiguration();
        this.configuration.setIp(ip);
        this.configuration.setListenPort(port);
        this.configuration.setAdminListenPort(adminPort);
        this.configuration.setMaxProcessorThreads(maxProcessorThreads);
        this.configuration.setIdleWaitSeconds(idleWaitSeconds);

        this.core = new AppServerCore(configuration);

        setupCoreInterceptors();
    }

    // 设置核心的拦截器
    private void setupCoreInterceptors() {
        this.core.addInterceptor(new DefaultExceptionInterceptor());
        this.core.addInterceptor(new HttpTestEnabledInterceptor());
        this.core.addInterceptor(new AuthticationInterceptor());
    }

    /////////////////////////////////////////

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public <T> T getProperty(String name) {
        return (T) this.properties.get(name);
    }

    /////////////////////////////////////////

    /**
     * 设置日志处理类。日志处理类用于对接口调用日志进行处理。日志处理类会在单独的线程中调用。
     *
     * @param invocationListener 日志处理类
     */
    public void setInvocationListener(InvocationListener invocationListener) {
        this.core.setInvocationListener(invocationListener);
    }

    /**
     * 设置 Action 包路径
     *
     * @param packages Action 包路径，可以有多个
     */
    public void setActionPackages(String... packages) {
        this.core.setPackages(packages);
    }

    /**
     * 设置 IP 白名单，缺省不限制客户端 IP
     *
     * @param ipAddresses 白名单 IP 地址
     */
    public void setIpWhiteList(String... ipAddresses) {
        this.configuration.setIpWhiteList(new ArrayList<>(Arrays.asList(ipAddresses)));
    }

    /**
     * 设置 Action 对象工厂
     *
     * @param actionFactory Action 对象工厂
     */
    public void setActionFactory(ActionFactory actionFactory) {
        this.core.setActionFactory(actionFactory);
    }

    public ActionFactory getActionFactory() {
        return this.core.getActionFactory();
    }

    /**
     * 设置是否允许通过浏览器调用接口。如果不允许浏览器调用接口，浏览器就只能查看接口文档。
     *
     * @param httpTestEnabled 是否允许通过浏览器调用接口
     */
    public void setHttpTestEnabled(boolean httpTestEnabled) {
        this.configuration.setHttpTestEnabled(httpTestEnabled);
    }

    /**
     * 设置服务器端鉴权。服务器端鉴权的实现必须和客户端一致。
     *
     * @param authenticator 服务器端鉴权
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.configuration.setAuthenticator(authenticator);
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    /////////////////////////////////////////

    public boolean isHttpTestEnabled() {
        return this.configuration.isHttpTestEnabled();
    }

    public Authenticator getAuthenticator() {
        return this.configuration.getAuthenticator();
    }

    public AppServerCore getCore() {
        return core;
    }

    /**
     * 获取 IoAcceptor 对象，用于获取状态和统计信息等
     *
     * @return IoAcceptor 对象
     */
    public IoAcceptor getMainAcceptor() {
        return mainAcceptor;
    }

    /////////////////////////////////////////

    /**
     * 启动服务器
     */
    public void start() {
        if (started) {
            LOG.warn("Already started.");
            return;
        }

        // 初始化 mainAcceptor
        initAcceptors();

        // 程序终止钩子(kill 或 Ctrl+C)
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);

        // 执行 ContextListener 初始化
        // 在这个地方仍然可以修改 configuration 的各项配置
        if (contextListener != null) {
            contextListener.initialize(configuration);
        }

        // 侦听端口，完成启动
        try {
            LOG.info("Starting server with ip " + configuration.getIp() + "...");

            mainAcceptor.setReuseAddress(true);
            mainAcceptor.bind(new InetSocketAddress(configuration.getIp(), configuration.getListenPort()));

            LOG.info("Mina application server listening at " + configuration.getListenPort() + "...");
            LOG.info("Mina application server started successfully. " +
                    "Server status: http://[server]:" + configuration.getAdminListenPort());
            started = true;
        } catch (Throwable e) {
            shutdown();
            throw new AppServerException(e);
        }
    }

    private void initAcceptors() {
        mainAcceptor = new NioSocketAcceptor(configuration.getMaxProcessorThreads());
        mainAcceptor.getFilterChain().addLast("logger", MinaUtils.createLoggingFilter());
        mainAcceptor.getFilterChain().addLast("ipWhiteListFilter", new IpWhiteListFilter(configuration.getIpWhiteList(), "json"));
        mainAcceptor.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(createJsonCodecFactory()));
        mainAcceptor.getFilterChain().addLast("performanceFilter", new IoPerformanceFilter(this.getSnapshot()));
        mainAcceptor.setHandler(createJsonHandler());
        mainAcceptor.getSessionConfig().setReadBufferSize(configuration.getBufferSize());
        mainAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, configuration.getIdleWaitSeconds());

        IoServiceMappings.addMappings(mainAcceptor.hashCode(), this);

        try {
            nanoHttpdServer = new NanoHttpdServer(this, configuration.getIp(), configuration.getAdminListenPort());
            nanoHttpdServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            throw new AppServerException(e);
        }
    }

    /**
     * 停止服务器。服务器是否会马上停止还取决于：
     * 1、ContextListener 执行花费的时间；
     * 2、剩余日志处理花费的时间。
     */
    private void shutdown() {
        if (!started) {
            return;
        }

        started = false;

        // 从这里开始服务器将拒绝一切后续请求
        this.core.setEnabled(false);

        // 关闭客户端连接
        LOG.info("Shutting down sessions...");
        closeSessions();

        // 执行侦听器
        if (contextListener != null) {
            try {
                contextListener.destroy(configuration);
            } catch (Throwable e) {
                LOG.error("服务器关闭预处理失败", e);
            }
        }

        // 关闭服务器
        core.shutdown();
        mainAcceptor.unbind();
        mainAcceptor.dispose(false);

        LOG.info("Mina App Server closed successfully. Good bye.\n\n");
        INSTANCES.remove(this);
    }

    /**
     * 关闭所有连接
     *
     */
    private void closeSessions() {
        for (IoSession session : mainAcceptor.getManagedSessions().values()) {
            session.closeNow();
        }
    }

    private DemuxingIoHandler createJsonHandler() {
        DemuxingIoHandler handler = new DemuxingIoHandler();
        handler.addSentMessageHandler(String.class, MessageHandler.NOOP);
        handler.addReceivedMessageHandler(JsonRequestMessage.class, new JsonRequestHandler(this));
        handler.addSentMessageHandler(JsonResponseMessage.class, MessageHandler.NOOP);
        handler.addExceptionHandler(Throwable.class, new ThrowableExceptionHandler());
        return handler;
    }

    private DemuxingProtocolCodecFactory createJsonCodecFactory() {
        DemuxingProtocolCodecFactory factory = new DemuxingProtocolCodecFactory();
        factory.addMessageDecoder(JsonRequestDecoder.class);
        factory.addMessageEncoder(JsonResponseMessage.class, JsonResponseEncoder.class);
        return factory;
    }

    // 关闭指定的 MinaAppServer 实例
    public static void shutdown(AppServerCore core) {
        MinaAppServer server = getInstance(core);

        if (server != null) {
            server.shutdown();
        }
    }

    // 查询与指定 AppServerCore 关联的 MinaAppServer 实例
    public static MinaAppServer getInstance(AppServerCore core) {
        MinaAppServer server = null;

        for (MinaAppServer instance : INSTANCES) {
            if (instance.core == core) {
                server = instance;
                break;
            }
        }
        return server;
    }

    /**
     * 设置服务启动与停止的侦听器
     *
     * @param contextListener 侦听器
     */
    public void setContextListener(ContextListener contextListener) {
        this.contextListener = contextListener;
    }

    private static class ThrowableExceptionHandler implements ExceptionHandler<Throwable> {

        public void exceptionCaught(IoSession ioSession, Throwable e) throws Exception {
            if (e instanceof IOException) {
                IO_LOG.info(e.toString());
            } else {
                IO_LOG.error("", e);
            }
        }
    }
}
