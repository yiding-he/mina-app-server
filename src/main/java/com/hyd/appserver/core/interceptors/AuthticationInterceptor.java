package com.hyd.appserver.core.interceptors;

import com.hyd.appserver.core.ActionInvocation;
import com.hyd.appserver.Authenticator;
import com.hyd.appserver.Interceptor;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.Protocol;

/**
 * 客户端认证
 *
 * @author yiding.he
 */
public class AuthticationInterceptor implements Interceptor {

    private final Authenticator authenticator;

    public AuthticationInterceptor(Authenticator authenticator) {
        if (authenticator == null) {
            throw new NullPointerException();
        }
        this.authenticator = authenticator;
    }

    @Override
    public Response intercept(ActionInvocation invocation) throws Exception {

        // 只对 socket 请求进行鉴权，对 http 请求放过
        if (invocation.getActionContext().getProtocol() == Protocol.Json) {
            Request request = invocation.getActionContext().getRequest();
            if (!authenticator.authenticate(request)) {
                return Response.fail("Authentication failed.");
            }
        }

        return invocation.invoke();
    }
}
