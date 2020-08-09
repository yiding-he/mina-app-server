package com.hyd.appserver;

import com.hyd.appserver.core.Protocol;
import com.hyd.appserver.core.ServerConfiguration;

import java.util.HashMap;

/**
 * Action 上下文
 *
 * @author yiding.he
 */
public class ActionContext extends HashMap<String, Object> {

    private static ThreadLocal<ActionContext> contextThreadLocal = new ThreadLocal<ActionContext>();

    public static void setContext(ActionContext context) {
        contextThreadLocal.set(context);
    }

    public static ActionContext getContext() {
        return contextThreadLocal.get();
    }

    /////////////////////////////////////////

    private ServerConfiguration serverConfiguration;

    private Response response;

    private Request request;

    private Action action;

    private Protocol protocol;
    
    private long executionStartMillis = 0;     // 执行开始时间， 0 表示未记录

    private long executionEndMillis = 0;       // 执行结束时间， 0 表示未记录

    public long getExecutionStartMillis() {
        return executionStartMillis;
    }

    public void setExecutionStartMillis(long executionStartMillis) {
        this.executionStartMillis = executionStartMillis;
    }

    public long getExecutionEndMillis() {
        return executionEndMillis;
    }

    public void setExecutionEndMillis(long executionEndMillis) {
        this.executionEndMillis = executionEndMillis;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public Request getRequest() {
        return request;
    }

    public Action getAction() {
        return action;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setServerConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }
}
