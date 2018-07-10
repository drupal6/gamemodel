package com.gl.actor;

public class RunnableRunner implements IRunner {

    private Runnable r;

    public RunnableRunner(Runnable r) {
        this.r = r;
    }

    @Override
    public Object run() {
        if( r != null) {
            r.run();
        }
        return null;
    }
}
