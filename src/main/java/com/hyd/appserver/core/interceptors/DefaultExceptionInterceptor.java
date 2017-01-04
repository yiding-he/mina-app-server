package com.hyd.appserver.core.interceptors;

import com.hyd.appserver.Interceptor;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.ActionInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 缺省错误处理
 *
 * @author yiding.he
 */
public class DefaultExceptionInterceptor implements Interceptor {

    private static final Logger LOG = LogManager.getLogger(DefaultExceptionInterceptor.class);

    @Override
    public Response intercept(ActionInvocation invocation) throws Exception {

        Request request = invocation.getActionContext().getRequest();

        try {
            return invocation.invoke();
        } catch (Exception e) {
            LOG.error("Action 执行失败" + getClientInfo(request), e);
            return Response.fail(e);
        }
    }

    private String getClientInfo(Request request) {
        if (request.getClientInfo() == null) {
            return "";
        }

        return "[" + request.getClientInfo().getIpAddress() + "/" +
                request.getClientInfo().getName() + "]";
    }

}
