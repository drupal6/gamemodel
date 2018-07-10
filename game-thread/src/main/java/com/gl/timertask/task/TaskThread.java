package com.gl.timertask.task;

import com.gl.timertask.timer.TimerThread;
import com.gl.timertask.timer.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class TaskThread extends Thread implements Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskThread.class);

    private String prefix;

    private LinkedBlockingQueue<Runnable> task_queue;

    private long heart = 0;

    private TimerThread timerThread;

    private boolean isRunning = false;

    public TaskThread(ThreadGroup group, String preFix) {
       this(group, preFix, 0);

    }

    public TaskThread(ThreadGroup group, String preFix, long heart) {
        super(group, "TaskThread-" + preFix);
        this.prefix = preFix;
        this.task_queue = new LinkedBlockingQueue<Runnable>();
        this.heart = heart;
        if(this.heart > 0) {
            timerThread = new TimerThread(this, prefix);
        }
    }

    @Override
    public void run() {
        if(heart > 0 && timerThread != null) {
            timerThread.start();
        }
        isRunning = true;
        while(isRunning && false == isInterrupted()) {
            Runnable t = task_queue.poll();
            if(t == null) {
                LOGGER.warn("thread{} run over task size({})", "TaskThread-" + prefix, task_queue.size());
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    long t1 = System.currentTimeMillis();
                    t.run();
                    long t2 = System.currentTimeMillis();
//                    if(t2 - t1 > 1000) {
//                        LOGGER.warn("thread{} run task({}) time({}), task size({})", "TaskThread-" + prefix, t.toString(), t2 - t1, task_queue.size());
//                    }
                    LOGGER.warn("thread{} run task({}) time({}), task size({})", "TaskThread-" + prefix, t.toString(), t2 - t1, task_queue.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop(boolean flag) {
        isRunning = false;
        if(timerThread != null) {
            timerThread.stop(flag);
        }
        task_queue.clear();
        try {
            synchronized (this) {
                notify();;
                interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executor(Runnable t) {
        try{
            if(task_queue.contains(t)) {
                return;
            }
            task_queue.add(t);
            synchronized (this) {
                notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTimeEvent(Event event) {
        if(timerThread != null) {
            timerThread.addEvent(event);
        }
    }

    public void remoeTimerEvent(Event event) {
        if(timerThread != null) {
            timerThread.removeEvent(event);
        }
    }

    public long getHeart() {
        return heart;
    }
}
