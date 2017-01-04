package com.hyd.appserver.core;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * (description)
 *
 * @author yiding.he
 */
public class DefaultActionFactory implements ActionFactory {

    static final Logger log = LogManager.getLogger(DefaultActionFactory.class);

    public Action getAction(Class<Action> type, Request request) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            log.error("创建 Action 失败", e);
            return null;
        }
    }
}
