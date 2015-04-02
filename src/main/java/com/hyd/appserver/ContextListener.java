package com.hyd.appserver;

import com.hyd.appserver.core.ServerConfiguration;

/**
 * 服务器启动和关闭时的侦听器
 *
 * @author yiding.he
 */
public interface ContextListener {

    void initialize(ServerConfiguration configuration);

    void destroy(ServerConfiguration configuration);
}
