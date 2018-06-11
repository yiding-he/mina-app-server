package com.hyd.appserver.core;

import com.hyd.appserver.Authenticator;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器设置。当服务器启动后，修改这里的值不会有任何效果。
 *
 * @author yiding.he
 */
@Data
@ConfigurationProperties(prefix = "mina-app-server")
public class ServerConfiguration {

    private String listenIp = "0.0.0.0";

    private int listenPort = 8090;

    private int adminPort = 9090;

    private int maxActiveWorkers = 10;

    private List<String> ipWhiteList = new ArrayList<>();

    private int readBufferSize = 4096;

    private int sessionIdleSeconds = 300;

    private boolean httpTestEnabled = false;

    private boolean autostart = false;

    private Authenticator authenticator;
}
