package com.gl.handler;

import org.apache.mina.core.session.IoSession;

public interface IHandler extends Runnable {

    IoSession getSession();

    void setSession(IoSession session);

    Object getMessage();

    void setMessage(Object message);

    long getCreateTime();

    void setCreateTime(long createTime);

    void setParameter(Object parameter);

    Object getParameter();
}
