package com.gl.actor;

public class ActorTask {
    public final IRunner runner;
    public final IActor actor;
    public final ICallback callback;

    public ActorTask(IRunner runner, IActor actor, ICallback callback) {
        this.runner = runner;
        this.actor = actor;
        this.callback = callback;
    }

    public ActorTask(IRunner runner) {
        this.runner = runner;
        this.actor = null;
        this.callback = null;
    }

    public ActorTask(Runnable r) {
        this(new RunnableRunner(r));
    }
}
