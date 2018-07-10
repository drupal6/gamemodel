package com.gl.group;

import com.gl.group.jedis.JedisPubSubMessage;

public interface IPubSubScript {

    /**
     * 消息处理
     * @param channel
     * @param message
     */
    public void onMessage(String channel, JedisPubSubMessage message);
}
