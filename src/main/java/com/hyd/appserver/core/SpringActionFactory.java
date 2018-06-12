package com.hyd.appserver.core;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SpringActionFactory implements ActionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SpringActionFactory.class);

    private ApplicationContext applicationContext;

    public SpringActionFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Action getAction(Class<? extends Action> type, Request request) {
        try {
            return applicationContext.getBean(type);
        } catch (BeansException e) {
            LOG.info(e.toString());
            return null;
        }
    }
}
