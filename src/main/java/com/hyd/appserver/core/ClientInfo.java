package com.hyd.appserver.core;

import java.io.Serializable;

/**
 * 接口调用方信息
 *
 * @author yiding.he
 */
public class ClientInfo implements Serializable {
    
    private String name;        // 调用方的名字，表明自己是哪个组件的，方便跟踪请求参数来源

    private String ipAddress;   // 调用方的 IP 地址

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
