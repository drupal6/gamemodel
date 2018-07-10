package com.gal.group.redis;

import com.gl.group.jedis.JedisManager;

public class TestRedisCluster {

    public static void main(String[] args) {
        JedisManager jedisManager = new JedisManager("D:/intellijw/java/gamemodel/game.redis/src/main/resources");
        JedisManager.setJedisManager(jedisManager);
        System.out.print(JedisManager.getinstance().get("foo"));
        JedisManager.getinstance().set("java", "javatest");
    }
}
