package com.hyd.appserver;

import com.hyd.appserver.core.InterceptorChain;

/**
 * @author yidin
 */
public interface MinaAppServerConfigurator {

    default Authenticator getAuthenticator() {
        return null;
    }

    default void configureInterceptors(InterceptorChain interceptors) {

    }
}
