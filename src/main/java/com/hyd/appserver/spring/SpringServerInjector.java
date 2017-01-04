package com.hyd.appserver.spring;

import com.hyd.appserver.AppServerException;
import com.hyd.appserver.MinaAppServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * 使用 Spring 上下文对象来初始化 Mina App Server。该上下文对象可以是已有的，也可以在这里新建。
 * 为了不让 MinaAppServer 类依赖 Spring，将相关的逻辑移出来
 *
 * 注意：系统没有使用 Spring 的情况下，绝对不可以执行到包含 SpringServerInjector 的语句上
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class SpringServerInjector {

    public static final String APPCONTEXT_PROPERTY_NAME = "spring-app-context";

    private static final Logger LOG = LogManager.getLogger(SpringServerInjector.class);

    /**
     * 使用已有的 ApplicationContext 来初始化 MinaAppServer
     *
     * @param server             MinaAppServer 实例
     * @param applicationContext Spring ApplicationContext
     */
    public static void init(MinaAppServer server, ApplicationContext applicationContext) {
        LOG.info("Setting spring action factory...");
        server.setProperty(APPCONTEXT_PROPERTY_NAME, applicationContext);

        SpringActionFactory actionFactory = new SpringActionFactory();
        actionFactory.setApplicationContext(applicationContext);
        server.setActionFactory(actionFactory);
    }

    /**
     * 根据指定的配置文件创建 Spring Application Context，并用其初始化 MinaAppServer
     *
     * @param server           MinaAppServer 实例
     * @param springConfigFile 配置文件路径
     */
    public static void inject(MinaAppServer server, String springConfigFile) {

        // 如果已经被 Spring 初始化过了，则跳过此步
        if (isSpringInjected(server)) {
            return;
        }

        LOG.info("Spring configuration file：" + springConfigFile);

        ApplicationContext context;

        if (springConfigFile.startsWith("classpath:")) {
            context = new ClassPathXmlApplicationContext(springConfigFile); // 本地 classpath 加载
        } else {
            context = new GenericXmlApplicationContext(springConfigFile);   // 可用于远程加载
        }

        init(server, context);
    }

    /**
     * 判断 MinaAppServer 实例是否已经被 Spring 初始化
     *
     * @param server MinaAppServer 实例
     *
     * @return 如果已经被 Spring 初始化，则返回 true
     */
    public static boolean isSpringInjected(MinaAppServer server) {
        return server.getProperty(APPCONTEXT_PROPERTY_NAME) != null &&
                server.getProperty(APPCONTEXT_PROPERTY_NAME) instanceof ApplicationContext;
    }

    /**
     * 获取指定类型的 bean
     *
     * @param className 类型名称
     *
     * @return bean 对象
     */
    public static <T> T getBeanByClass(MinaAppServer appServer, String className) {
        ApplicationContext context = appServer.getProperty(APPCONTEXT_PROPERTY_NAME);

        try {
            if (context == null) {
                return null;
            } else {
                return (T) context.getBean(Class.forName(className));
            }
        } catch (ClassNotFoundException e) {
            throw new AppServerException("", e);
        }
    }
}
