package com.hyd.appserver.core.interceptors;

import com.hyd.appserver.core.ActionInvocation;
import com.hyd.appserver.Interceptor;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.Protocol;

/**
 * 当 HTTP 接口调用被禁用时，返回失败信息
 *
 * @author yiding.he
 */
public class HttpTestEnabledInterceptor implements Interceptor {

    @Override
    public Response intercept(ActionInvocation invocation) throws Exception {
        if (invocation.getActionContext().getProtocol() == Protocol.Http &&
                !invocation.getActionContext().getServerConfiguration().isHttpTestEnabled()) {
            return Response.fail("当前禁止通过浏览器调用接口。浏览器只能查看文档。");
        }

        return invocation.invoke();
    }
}
