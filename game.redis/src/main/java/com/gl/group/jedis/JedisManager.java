package com.gl.group.jedis;

import com.gl.util.FileUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis集群管理器
 */
public class JedisManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisManager.class);

    private static JedisCluster jedisCluster;

    private static JedisManager jedisManager;

    public JedisManager(String configPath) {
        this(loadJedisClusterConfig(configPath));
    }

    public JedisManager(JedisClusterConfig config) {
        HashSet<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        for(JedisClusterNodeConfig nodeConfig : config.getNodes()) {
            if (nodeConfig.getIp() != null && nodeConfig.getIp().length() > 5) {
                jedisClusterNodes.add(new HostAndPort(nodeConfig.getIp(), nodeConfig.getPort()));
            }
        }
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.getPoolMaxTotal());
        poolConfig.setMaxIdle(config.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        poolConfig.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        poolConfig.setSoftMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        poolConfig.setTestOnBorrow(config.isTestOnBorrow());
        poolConfig.setTestWhileIdle(config.isTestWhileIdle());
        poolConfig.setTestOnReturn(config.isTestOnReturn());
        jedisCluster = new JedisCluster(jedisClusterNodes, config.getConnectionTimeout(), config.getSoTimeout(), config.getMaxRedisrections(), poolConfig);
    }

    private static JedisClusterConfig loadJedisClusterConfig(String configPath) {
        JedisClusterConfig jedisClusterConfig = FileUtil.getConfigXML(configPath, "jedisClusterConfig.xml", JedisClusterConfig.class);
        if(jedisClusterConfig == null) {
            LOGGER.error("redis配置{}未找到", configPath);
            System.exit(1);
        }
//        JedisClusterConfig jedisClusterConfig = new JedisClusterConfig();
//        HashSet<JedisClusterNodeConfig> nodes = new HashSet<>();
//        for(int i = 7000; i < 7006; i++) {
//            JedisClusterNodeConfig node1 = new JedisClusterNodeConfig();
//            node1.setIp("192.168.2.101");
//            node1.setPort(i);
//            nodes.add(node1);
//        }
//        jedisClusterConfig.setNodes(nodes);
        return jedisClusterConfig;
    }

    public static JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public static JedisManager getinstance() {
        return jedisManager;
    }

    public static void setJedisManager(JedisManager jedisManager) {
        JedisManager.jedisManager = jedisManager;
    }

    public Map<String, String> hgetAll(final String key) {
        Map<String, String> hgetAll = getJedisCluster().hgetAll(key);
        if (hgetAll == null) {
            return null;
        }
        return hgetAll;
    }

    public String hget(final String key, final Object field) {
        String hget = getJedisCluster().hget(key, field.toString());
        if (hget == null) {
            return null;
        }
        return hget;
    }

    public Long hset(final String key, final Object field, final String value) {
        return getJedisCluster().hset(key, field.toString(), value);
    }

    public String set(final String key, final String value) {
        return getJedisCluster().set(key, value);
    }

    public String get(final String key) {
        return getJedisCluster().get(key);
    }
}
