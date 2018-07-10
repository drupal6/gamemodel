package com.gl.task;

public abstract class ITask implements Runnable {

    protected long createTime;

    public ITask() {
        this.createTime = System.nanoTime();
    }

    public long getCreateTime() {
        return createTime;
    }
}
