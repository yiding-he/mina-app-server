package com.hyd.appserver;

/**
 * 处理接口调用信息，用于记录和统计需要
 *
 * @author yiding.he
 */
public interface InvocationListener {

    /**
     * 处理接口调用，接口调用完成后将执行这个方法
     *
     * @param context 上下文，包括请求、回应等
     *
     * @throws Exception 如果处理失败
     */
    void invocationFinished(ActionContext context) throws Exception;
}
