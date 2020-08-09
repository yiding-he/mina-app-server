package com.hyd.appserver;

import com.hyd.appserver.core.*;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 应用服务器端。
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class MinaAppServer {

    public static final String VERSION_STRING = "3.0.1";

    private static final Logger LOG = LoggerFactory.getLogger(MinaAppServer.class);

    static final Logger IO_LOG = LoggerFactory.getLogger("java.io.mina.exceptions");

    private static final List<MinaAppServer> INSTANCES = new ArrayList<>();

    //////////////// 其他成员 ////////////////////

    private boolean started = false;

    private ServerConfiguration configuration;

    private ApplicationContext applicationContext;

    private AppServerCore core;

    private NioSocketAcceptor mainAcceptor;

    private NanoHttpdServer nanoHttpdServer;

    private Thread shutdownHookThread = new Thread(this::shutdown);

    private ContextListener contextListener;

    private Snapshot snapshot = new Snapshot();


    /**
     * 构造方法
     *
     * @param serverConfiguration 服务器配置属性
     */
    public MinaAppServer(ServerConfiguration serverConfiguration) {
        this.configuration = serverConfiguration;
        this.core = new AppServerCore(serverConfiguration);

        this.nanoHttpdServer = new NanoHttpdServer(
                this, configuration.getListenIp(), configuration.getAdminPort());
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.core.setActionFactory(new SpringActionFactory(applicationContext));
    }

    // 设置核心的拦截器
    private void setupCoreInterceptors(Authenticator authenticator) {
        InterceptorChain interceptors = this.core.getInterceptors();
        interceptors.add(new DefaultExceptionInterceptor());
        interceptors.add(new HttpTestEnabledInterceptor());

        if (authenticator != null) {
            interceptors.add(new AuthticationInterceptor(authenticator));
        }
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

    public Snapshot getSnapshot() {
        return snapshot;
    }

    /////////////////////////////////////////

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
        configure();

        // 程序终止钩子(kill 或 Ctrl+C)
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);

        // 执行 ContextListener 初始化
        // 在这个地方仍然可以修改 configuration 的各项配置
        if (contextListener != null) {
            contextListener.initialize(configuration);
        }

        // 侦听端口，完成启动
        try {
            LOG.info("Starting server with ip " + configuration.getListenIp() + "...");

            mainAcceptor.setReuseAddress(true);
            mainAcceptor.bind(new InetSocketAddress(configuration.getListenIp(), configuration.getListenPort()));

            LOG.info("Mina application server listening at " + configuration.getListenPort() + "...");
            LOG.info("Mina application server started successfully. " +
                    "Server status: http://[server]:" + configuration.getAdminPort());
            started = true;
        } catch (Throwable e) {
            shutdown();
            throw new AppServerException(e);
        }
    }

    private void configure() {
        initInterceptors();
        initAcceptors();
        initFunctionTypeMappings();
    }

    private void initFunctionTypeMappings() {
        this.core.setTypeMappings(new SpringFunctionTypeMappings(applicationContext));
    }

    private void initInterceptors() {

        if (applicationContext == null) {
            return;
        }

        try {
            MinaAppServerConfigurator configurator = applicationContext.getBean(MinaAppServerConfigurator.class);
            LOG.info("Applying " + configurator.getClass().getCanonicalName() + "...");

            setupCoreInterceptors(configurator.getAuthenticator());             // core interceptors
            configurator.configureInterceptors(this.core.getInterceptors());    // user defined interceptors
            this.setInvocationListener(configurator.getInvocationListener());
        } catch (NoSuchBeanDefinitionException e) {
            // configurator not exist, ignore this
        } catch (BeansException e) {
            LOG.error("Unable to get bean MinaAppServerConfigurator: " + e.toString());
        }
    }

    private void initAcceptors() {
        mainAcceptor = new NioSocketAcceptor(configuration.getMaxActiveWorkers());
        mainAcceptor.getFilterChain().addLast("logger", MinaUtils.createLoggingFilter());
        mainAcceptor.getFilterChain().addLast("ipWhiteListFilter", new IpWhiteListFilter(configuration.getIpWhiteList(), "json"));
        mainAcceptor.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(createJsonCodecFactory()));
        mainAcceptor.getFilterChain().addLast("performanceFilter", new IoPerformanceFilter(this.getSnapshot()));
        mainAcceptor.setHandler(createJsonHandler());
        mainAcceptor.getSessionConfig().setReadBufferSize(configuration.getReadBufferSize());
        mainAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, configuration.getSessionIdleSeconds());

        IoServiceMappings.addMappings(mainAcceptor.hashCode(), this);

        try {
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

    public List<Action> getActionBeans() {

        if (this.applicationContext == null) {
            return Collections.emptyList();
        }

        ArrayList<Action> actions = new ArrayList<>(this.applicationContext.getBeansOfType(Action.class).values());
        actions.sort(Comparator.comparing(Action::getFullFunctionPath));
        return actions;
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
