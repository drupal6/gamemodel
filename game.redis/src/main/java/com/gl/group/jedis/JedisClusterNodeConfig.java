package com.gl.group.jedis;

import org.simpleframework.xml.Element;

import java.io.Serializable;

public class JedisClusterNodeConfig implements Serializable {
    @Element(required = true)
    private String ip;
    @Element(required = true)
    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
