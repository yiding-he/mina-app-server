package com.hyd.appserver.core;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (description)
 *
 * @author yiding.he
 */
public class DefaultActionFactory implements ActionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultActionFactory.class);

    public Action getAction(Class<Action> type, Request request) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            LOG.error("创建 Action 失败", e);
            return null;
        }
    }
}
