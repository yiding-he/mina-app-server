package com.hyd.appserver.core;

import com.hyd.appserver.ContextListener;
import com.hyd.appserver.Interceptor;
import com.hyd.appserver.LogHandler;
import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.authentication.BasicAuthenticator;
import com.hyd.appserver.spring.SpringServerInjector;
import com.hyd.appserver.utils.StringUtils;

import java.util.Properties;

/**
 * AppServer 实例化工厂
 *
 * @author yiding.he
 */
public class AppServerFactory {

    public static final String PROPERTY_IP = "ip";

    public static final String PROPERTY_PORT = "port";

    public static final String PROPERTY_ADMIN_PORT = "admin-port";

    public static final String PROPERTY_MAX_ACTIVE_WORKERS = "max-active-workers";

    public static final String PROPERTY_IDLE_TIMEOUT = "idle-timeout";

    public static final String PROPERTY_ACTION_PACKAGES = "action-packages";

    public static final String PROPERTY_SPRING_CONFIG = "spring-config";

    public static final String PROPERTY_CONTEXT_LISTENER = "context-listener";

    public static final String PROPERTY_LOG_HANDLER = "log-handler";

    public static final String PROPERTY_INTERCEPTORS = "interceptors";

    public static final String PROPERTY_USERNAME = "username";

    public static final String PROPERTY_PASSWORD = "password";

    public static final String PROPERTY_HTTPTEST_ENABLED = "http-test-enabled";

    /**
     * 创建一个 AppServer 实例
     *
     * @param properties 实例化参数，具体参见本类 PROPERTY_ 开头的常量
     *
     * @return AppServer 实例
     */
    public static MinaAppServer createServer(Properties properties) {

        // 主要配置
        String ip = getProperty(properties, PROPERTY_IP, ServerConfiguration.DEFAULT_LISTEN_IP);

        int port = Integer.parseInt(
                getProperty(properties, PROPERTY_PORT, String.valueOf(ServerConfiguration.DEFAULT_LISTEN_PORT)));

        int adminPort = Integer.parseInt(
                getProperty(properties, PROPERTY_ADMIN_PORT, String.valueOf(port + 1000)));

        int maxActiveWorkers = Integer.parseInt(
                getProperty(properties, PROPERTY_MAX_ACTIVE_WORKERS, String.valueOf(ServerConfiguration.DEFAULT_MAX_PROCESSORS)));

        int idleTimeout = Integer.parseInt(
                getProperty(properties, PROPERTY_IDLE_TIMEOUT, String.valueOf(ServerConfiguration.DEFAULT_IDLE_WAIT)));

        MinaAppServer server = new MinaAppServer(ip, port, adminPort, maxActiveWorkers, idleTimeout);

        // 其他配置
        configServer(properties, server);

        return server;
    }

    protected static String getProperty(Properties properties, String key, String defaultValue) {
        Object value = properties.get(key);

        if (value == null) {
            return defaultValue;
        } else {
            return value.toString();
        }
    }

    // 应用服务器配置
    protected static void configServer(Properties properties, MinaAppServer server) {

        // spring configuration file
        // should be placed first
        String springConfigFile = properties.getProperty(PROPERTY_SPRING_CONFIG);
        if (!StringUtils.isEmpty(springConfigFile)) {
            SpringServerInjector.inject(server, springConfigFile);
        }

        // action package
        String packages = properties.getProperty(PROPERTY_ACTION_PACKAGES);
        if (!StringUtils.isEmpty(packages)) {
            String[] actionPackages = packages.split(",");
            server.setActionPackages(actionPackages);
        }

        // listener
        String contextListenerClassName = properties.getProperty(PROPERTY_CONTEXT_LISTENER);
        if (!StringUtils.isEmpty(contextListenerClassName)) {
            initListener(server, contextListenerClassName);
        }

        // log handler
        String logHandlerClassName = properties.getProperty(PROPERTY_LOG_HANDLER);
        if (!StringUtils.isEmpty(logHandlerClassName)) {
            initLogHandler(server, logHandlerClassName);
        }

        // interceptors
        String interceptors = properties.getProperty(PROPERTY_INTERCEPTORS);
        if (!StringUtils.isEmpty(interceptors)) {
            initInterceptors(server, interceptors.trim());
        }

        // authentication
        String username = properties.getProperty(PROPERTY_USERNAME);
        String password = properties.getProperty(PROPERTY_PASSWORD);
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            final BasicAuthenticator auth = new BasicAuthenticator();
            auth.putKeyMapping(username, password);
            server.setAuthenticator(auth);
        }

        // HTTP test enabled
        String httpTestEnabled = properties.getProperty(PROPERTY_HTTPTEST_ENABLED);
        if (!StringUtils.isEmpty(httpTestEnabled)) {
            server.setHttpTestEnabled("true".equalsIgnoreCase(httpTestEnabled));
        } else {
            server.setHttpTestEnabled(ServerConfiguration.DEFAULT_HTTP_TEST_ENABLED);
        }
    }

    protected static void initInterceptors(MinaAppServer server, String interceptors) {
        String[] classNames = interceptors.split(",");

        for (String className : classNames) {
            if (!StringUtils.isEmpty(className)) {

                Interceptor interceptor = BeanFactory.getBeanByClass(server, className);

                if (interceptor != null) {
                    server.getCore().addInterceptor(interceptor);
                }
            }
        }
    }

    protected static void initLogHandler(MinaAppServer server, String logHandlerClassName) {
        LogHandler handler = BeanFactory.getBeanByClass(server, logHandlerClassName);
        if (handler != null) {
            server.setLogHandler(handler);
        }
    }

    protected static void initListener(MinaAppServer server, String contextListenerClassName) {
        ContextListener listener = BeanFactory.getBeanByClass(server, contextListenerClassName);
        if (listener != null) {
            server.setContextListener(listener);
        }
    }
}
