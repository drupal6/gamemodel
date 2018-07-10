package com.gl.group.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * redis监听事件
 */
public class JedisPublistener extends JedisPubSub implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisPublistener.class);

    private String[] channels;

    public JedisPublistener(String ... channels) {
        this.channels = channels;
    }

    @Override
    public void onMessage(String channel, String message) {

    }

    @Override
    public void onSubscribe(String channel, int subscirbeChannels) {
        super.onSubscribe(channel, subscirbeChannels);
        LOGGER.info("onSubscirbe:{},{}", channel, subscirbeChannels);
    }

    public void start() {
        Thread thread = new Thread(this, "JedisPubSub");
        thread.start();
    }

    public void stop() {
        unsubscribe();
    }

    @Override
    public void run() {
        if(channels != null && channels.length > 0) {

        }
    }
}
