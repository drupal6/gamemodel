package com.gl.actor.impl;

import com.gl.actor.ActorTask;
import com.gl.actor.IActor;
import com.gl.actor.ICallback;
import com.gl.actor.IRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Actor implements IActor {

    private final Logger LOGGER = LoggerFactory.getLogger(Actor.class);

    private final BlockingQueue<ActorTask> taskQueue;

    private Thread t;

    private AtomicBoolean running = new AtomicBoolean(false);

    private String name;

    private AtomicBoolean stopWhenEmpty = new AtomicBoolean(false);

    private AtomicInteger maxTaskCount = new AtomicInteger(0);

    public Actor(String name, int capacity) {
        this.name = name;
        if(capacity <= 0) {
            this.taskQueue = new LinkedBlockingDeque<ActorTask>();
        } else {
            this.taskQueue = new ArrayBlockingQueue<ActorTask>(capacity);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "|" + getThreadName() + "|" + getThreadId() + "|" + getQueuesize();
    }

    @Override
    public boolean start() {
        if(running.get()) {
            return false;
        }
        running.set(true);
        t = new Thread(new TaskRunner(), name);
        t.start();
        return true;
    }

    @Override
    public void clear() {
        taskQueue.clear();
    }

    @Override
    public void stop() {
        if(running.get() == false) {
            return;
        }
        running.set(false);
        clear();
        t.interrupt();
    }

    @Override
    public void stopWhenEmpty() {
        if(stopWhenEmpty.get()) {
            return;
        }
        stopWhenEmpty.set(true);
    }

    @Override
    public void waitForStop() {
        while (isRunning()) {
            try{
                Thread.sleep(500);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public boolean put(IRunner runner) {
        return put(runner, null, null, 0);
    }

    @Override
    public boolean put(IRunner runner, long millies) {
        return put(runner, null, null, millies);
    }

    @Override
    public boolean put(IRunner runner, ICallback callback, IActor target) {
        return put(runner, callback, target, 0);
    }

    @Override
    public boolean put(IRunner runner, ICallback callback, IActor target, long millies) {
        if(stopWhenEmpty.get()) {
            return false;
        }
        if(false == running.get()) {
            return false;
        }
        //当前线程
        if(Thread.currentThread() == t) {
            ActorTask actorTask = new ActorTask(runner, target, callback);
            try{
                runTask(actorTask);
            }catch (Exception e) {
                LOGGER.error("", e);
            }
            return true;
        }
        try {
            int size = taskQueue.size();
            if(maxTaskCount.get() < size) {
                maxTaskCount.set(size);
            }
            ActorTask actorTask = new ActorTask(runner, target, callback);
            if(millies == 0) {
               return taskQueue.offer(actorTask);
            } else if(millies > 0) {
                return taskQueue.offer(actorTask, millies, TimeUnit.MILLISECONDS);
            } else {
                taskQueue.put(actorTask);
                return true;
            }
        }catch (Exception e) {
            LOGGER.error("", e);
        }
        return false;
    }

    @Override
    public long getThreadId() {
        return t.getId();
    }

    @Override
    public String getThreadName() {
        return t.getName();
    }

    @Override
    public int getMaxQueueSize() {
        return maxTaskCount.get();
    }

    @Override
    public int getQueuesize() {
        return taskQueue.size();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isStopping() {
        return stopWhenEmpty.get();
    }

    private class TaskRunner implements Runnable {
        @Override
        public void run() {
            while (running.get()) {
                try{
                    if(stopWhenEmpty.get() && taskQueue.isEmpty()) {
                        running.set(false);
                        break;
                    }
                    final ActorTask task = taskQueue.poll(1000l, TimeUnit.MILLISECONDS);
                    runTask(task);
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    private void runTask(final ActorTask task) {
        if(task == null) {
            return;
        }
        final Object result = task.runner == null ? null : task.runner.run();
        if(task.callback != null && task.actor != null) {
            task.actor.put(new IRunner() {
                @Override
                public Object run() {
                    task.callback.onResult(result);
                    return null;
                }
            });
        } else if(task.callback != null) {
            task.callback.onResult(result);
        }
    }
}
