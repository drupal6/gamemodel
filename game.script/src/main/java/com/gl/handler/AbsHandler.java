package com.gl.handler;

import org.apache.mina.core.session.IoSession;

import java.nio.channels.Channel;

/**
 * 抽象handler
 */
public abstract class AbsHandler implements IHandler{
    protected IoSession session;
    protected long createTime;
    protected Channel channel;


    @Override
    public IoSession getSession() {
        return session;
    }

    @Override
    public void setSession(IoSession session) {
        this.session = session;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(long createTime) {

        this.createTime = createTime;
    }

    @Override
    public void setParameter(Object parameter) {

    }

    @Override
    public Object getParameter() {
        return null;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
