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

    @Override
    public Response intercept(ActionInvocation invocation) throws Exception {

        // 只对 socket 请求进行鉴权，对 http 请求放过
        if (invocation.getActionContext().getProtocol() == Protocol.Json) {

            Authenticator authenticator =
                    invocation.getActionContext().getServerConfiguration().getAuthenticator();

            Request request = invocation.getActionContext().getRequest();
            boolean needAuthentication = authenticator != null;

            if (needAuthentication && !authenticator.authenticate(request)) {
                return Response.fail("Authentication failed.");
            }
        }

        return invocation.invoke();
    }
}
