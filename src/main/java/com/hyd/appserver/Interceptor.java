package com.hyd.appserver;

import com.hyd.appserver.core.ActionInvocation;

/**
 * 执行 Action 前的处理
 *
 * @author yiding.he
 */
public interface Interceptor {

    Response intercept(ActionInvocation invocation) throws Exception;
}
