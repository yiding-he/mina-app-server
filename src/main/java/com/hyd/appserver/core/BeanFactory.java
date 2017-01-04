package com.hyd.appserver.core;

import com.hyd.appserver.AppServerException;
import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.spring.SpringServerInjector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 从 Spring 容器获取 Bean 对象，或者创建一个
 *
 * @author yiding.he
 */
public class BeanFactory {

    private static final Logger LOG = LogManager.getLogger(BeanFactory.class);

    @SuppressWarnings("unchecked")
    public static <T> T getBeanByClass(MinaAppServer minaAppServer, String className) throws AppServerException {

        T result = null;

        if (minaAppServer.getProperty("spring-app-context") != null) {
            try {
                result = SpringServerInjector.getBeanByClass(minaAppServer, className);
            } catch (Exception e) {
                LOG.info("Class '" + className + "' not found in Spring context.");
            }
        }

        if (result == null) {
            try {
                result = (T) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new AppServerException(e);
            }
        }

        return result;
    }
}
