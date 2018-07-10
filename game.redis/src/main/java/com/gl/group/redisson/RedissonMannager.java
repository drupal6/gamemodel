package com.gl.group.redisson;

import com.gl.util.FileUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RedissonMannager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonMannager.class);

    private static RedissonClusterConfig redissonClusterConfig;

    private static RedissonClient redisson;

    public RedissonMannager() {}

    public static void connectRedis(String configPaht) {
        if(redisson != null) {
            LOGGER.warn("Redisson客户端已经连接");
        }
//        redissonClusterConfig = FileUtil.getConfigXML(configPaht, "redissonClusterConfig.xml", RedissonClusterConfig.class);
//        if(redissonClusterConfig == null) {
//            LOGGER.error("{}/redissonClusterConfig.xml文件不存在", configPaht);
//            System.exit(0);
//        }
        redissonClusterConfig = new RedissonClusterConfig();
        List<String> nodes = new ArrayList<>();
        for(int i = 7000; i < 7006; i++) {
            nodes.add("redis://192.168.2.101:" + i);
        }
        redissonClusterConfig.setNodes(nodes);
        Config config = new Config();
        config.setCodec(new FastJsonCodec());
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        clusterServersConfig.setScanInterval(redissonClusterConfig.getScanInterval());
        for(String url : redissonClusterConfig.getNodes()) {
            clusterServersConfig.addNodeAddress(url);
        }
        clusterServersConfig.setReadMode(redissonClusterConfig.getReadMode());
        clusterServersConfig.setSubscriptionMode(redissonClusterConfig.getSubscriptionMode());
        redisson = Redisson.create(config);
    }

    public static RedissonClient getRedisssonClient() {
        return redisson;
    }
}
