package com.hyd.appserver;

/**
 * 每个业务接口都要实现 Action。
 *
 * @author yiding.he
 */
public abstract interface Action<Q extends Request, R extends Response> {

    R execute(Q request) throws Exception;
}
