package com.hyd.appserver;

/**
 * [description]
 *
 * @author yiding.he
 */
public interface ClientInvocation {

    MinaAppClient getClient();

    Request getRequest();

    Response invoke();
}
