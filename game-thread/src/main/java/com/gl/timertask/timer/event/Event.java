package com.gl.timertask.timer.event;

public abstract class Event implements Runnable {

    private long end;

    private int loop = -1;

    public Event(long end) {
        this(end, -1);
    }

    public Event(long end, int loop) {
        this.end = end;
        this.loop = loop;
    }

    public long remain() {
        if(end == 0 || loop < 0) {
            return 0;
        }
        return end - System.currentTimeMillis();
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }
}
