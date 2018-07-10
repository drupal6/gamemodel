package com.gl.group.jedis;

import org.simpleframework.xml.Element;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.HashSet;

/**
 * redis集群配置
 */
public class JedisClusterConfig implements Serializable {

    @Element(required =  false)
    private HashSet<JedisClusterNodeConfig> nodes = new HashSet<JedisClusterNodeConfig>();

    @Element(required =  true)
    private int poolMaxTotal = 500;

    @Element(required =  true)
    private int poolMaxIdle = 5;

    @Element(required =  true)
    private int connectionTimeout = 2000;

    @Element(required =  true)
    private int soTimeout = 2000;

    @Element(required =  true)
    private int maxRedisrections = 6;

    @Element(required =  true)
    private int timeBetweenEvictionRunsMillis = 30000;

    @Element(required =  true)
    private int minEvictableIdleTimeMillis = 1800000;

    @Element(required =  true)
    private int maxWaitMillis = 60000;

    @Element(required =  true)
    private boolean testOnBorrow = true;

    @Element(required =  true)
    private boolean testWhileIdle = false;

    @Element(required =  true)
    private boolean testOnReturn = false;

    public HashSet<JedisClusterNodeConfig> getNodes() {
        return nodes;
    }

    public void setNodes(HashSet<JedisClusterNodeConfig> nodes) {
        this.nodes = nodes;
    }

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public void setPoolMaxIdle(int poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxRedisrections() {
        return maxRedisrections;
    }

    public void setMaxRedisrections(int maxRedisrections) {
        this.maxRedisrections = maxRedisrections;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

}
