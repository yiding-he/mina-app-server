package com.hyd.appserver.core;

import com.hyd.appserver.ActionContext;
import com.hyd.appserver.Interceptor;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.InterceptorChain;

/**
 * (description)
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class ActionInvocation {

    private InterceptorChain interceptors;
    
    private ActionContext actionContext;

    private FinalInvocation finalInvocation;

    public ActionInvocation(
            ActionContext actionContext, 
            InterceptorChain interceptors,
            FinalInvocation finalInvocation
    ) {
        this.actionContext = actionContext;
        this.interceptors = interceptors;
        this.finalInvocation = finalInvocation;
    }

    // 真正的执行 Action
    public Response invoke() throws Exception {
        Interceptor next = interceptors.next();
        
        if (next != null) {
            return next.intercept(this);
            
        } else {
            Response response;
            
            // 执行 execute 方法，并记录执行开始时间和执行完成时间
            try {
                actionContext.setExecutionStartMillis(System.currentTimeMillis());
                response = finalInvocation.invoke();
            } finally {
                actionContext.setExecutionEndMillis(System.currentTimeMillis());
            }
            
            return response;
        }
    }

    public InterceptorChain getInterceptors() {
        return interceptors;
    }

    public ActionContext getActionContext() {
        return actionContext;
    }
    
    /////////////////////////////////////////
    
    public static interface FinalInvocation {

        Response invoke() throws Exception;
    }
}
