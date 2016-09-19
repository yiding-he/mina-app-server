package com.hyd.appserver.spring;

import com.hyd.appserver.DefaultServerMain;
import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.core.AppServerFactory;
import com.hyd.appserver.core.ServerConfiguration;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Properties;

/**
 * (description)
 * created at 2014/11/24
 *
 * @author Yiding
 */
public class SpringAppSeverFactory extends AppServerFactory {

    /**
     * 创建一个 AppServer 实例
     *
     * @param configResourcePath MinaAppServer 配置文件资源路径
     * @param applicationContext Spring 上下文
     *
     * @return AppServer 实例
     *
     * @throws IOException 如果读取配置文件失败
     */
    public static MinaAppServer createServer(
            String configResourcePath, ApplicationContext applicationContext)
            throws IOException {

        Properties properties = DefaultServerMain.combineArgumentsAndProperties(null, configResourcePath);
        return createServer(properties, applicationContext);
    }

    /**
     * 创建一个 AppServer 实例
     *
     * @param properties         实例化参数，具体参见本类 PROPERTY_ 开头的常量
     * @param applicationContext 已有的 ApplicationContext 对象
     *
     * @return AppServer 实例
     */
    public static MinaAppServer createServer(Properties properties, ApplicationContext applicationContext) {
        int port = Integer.parseInt(
                getProperty(properties, PROPERTY_PORT, String.valueOf(ServerConfiguration.DEFAULT_LISTEN_PORT)));

        int adminPort = Integer.parseInt(
                getProperty(properties, PROPERTY_ADMIN_PORT, String.valueOf(port + 1000)));

        int maxActiveWorkers = Integer.parseInt(
                getProperty(properties, PROPERTY_MAX_ACTIVE_WORKERS, String.valueOf(ServerConfiguration.DEFAULT_MAX_PROCESSORS)));

        int idleTimeout = Integer.parseInt(
                getProperty(properties, PROPERTY_IDLE_TIMEOUT, String.valueOf(ServerConfiguration.DEFAULT_IDLE_WAIT)));

        MinaAppServer server = new MinaAppServer(properties.getProperty("ip"), port, adminPort, maxActiveWorkers, idleTimeout);
        SpringServerInjector.init(server, applicationContext);

        configServer(properties, server);
        return server;
    }

}
