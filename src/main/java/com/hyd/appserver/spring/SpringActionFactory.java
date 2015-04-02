package com.hyd.appserver.spring;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.core.ActionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * (description)
 *
 * @author yiding.he
 */
public class SpringActionFactory implements ActionFactory {

    private ApplicationContext applicationContext;

    public Action getAction(Class<Action> actionClass, Request request) {
        return applicationContext.getBean(actionClass);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}