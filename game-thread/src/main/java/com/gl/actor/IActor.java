package com.gl.actor;

public interface IActor {

    boolean start();

    void clear();

    void stop();

    void stopWhenEmpty();

    void waitForStop();

    boolean put(IRunner runner);

    boolean put(IRunner runner, long millies);

    boolean put(IRunner runner, ICallback callback, IActor target);

    boolean put(IRunner runner, ICallback callback, IActor target, long millies);

    long getThreadId();

    String getThreadName();

    int getMaxQueueSize();

    int getQueuesize();

    boolean isRunning();

    boolean isStopping();
}
