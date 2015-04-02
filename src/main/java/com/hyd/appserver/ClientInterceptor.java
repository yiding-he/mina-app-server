package com.hyd.appserver;

/**
 * 客户端的拦截器，用于请求之前的处理
 *
 * @author yiding.he
 */
public interface ClientInterceptor {

    Response intercept(ClientInvocation invocation);
}
