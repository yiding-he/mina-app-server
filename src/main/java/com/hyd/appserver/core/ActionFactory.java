package com.hyd.appserver.core;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;

/**
 * 构造 Action 对象的工厂
 *
 * @author yiding.he
 */
public interface ActionFactory {

    Action getAction(Class<? extends Action> type, Request request);
}
