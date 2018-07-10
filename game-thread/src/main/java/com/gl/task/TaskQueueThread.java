package com.gl.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueueThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueThread.class);
    //线程前缀
    private String prefix;
    //队列最大个数
    private int maxQSize;
    //任务队列
    private LinkedBlockingQueue<ITask> task_queue;

    private volatile boolean isRunning = false;

    public TaskQueueThread(ThreadGroup group, String prefix) {
        this(group, prefix, 1024);
    }

    public TaskQueueThread(ThreadGroup group, String prefix, int maxQSie) {
        super(group,"TaskQueueThread-" + prefix);
        this.prefix = prefix;
        this.maxQSize = maxQSie;
        task_queue = new LinkedBlockingQueue<ITask>();
        isRunning = true;
    }

    public void stop(boolean flag) {
        if(isRunning) {
            isRunning = false;
            synchronized (this) {
                notify();
                interrupt();
            }
            task_queue.clear();
            LOGGER.warn("TaskQueueThread-{} stoped.",  prefix);
        }
    }

    public void addTask(ITask r) {
        if(isRunning && false == isInterrupted()) {
            if(task_queue.contains(r)) {
                return;
            }
            task_queue.add(r);
            synchronized (this) {
                notify();;
            }
        }
    }

    @Override
    public void run() {
        while (isRunning && false == isInterrupted()) {
            ITask r = task_queue.poll();
            if(r == null) {
                try{
                    synchronized (this) {
                        wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try{
                    long starT = System.nanoTime();
                    r.run();
                    long endT = System.nanoTime();
                    if(endT - starT > 1000000000) {
                        LOGGER.warn("{} run time({}) is bagger than 1s. queue.szie:{}", r.toString(), endT - starT, task_queue.size());
                    }
                    if(starT - r.getCreateTime() > 1000000000) {
                        LOGGER.warn("{} wait time({}) is bagger than 1s.. queue.szie:{}", r.toString(), starT - r.getCreateTime(), task_queue.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int size = task_queue.size();
        if(size >= maxQSize) {
            LOGGER.error("Queue.size({}) is bigger than setting({}).",  size, maxQSize);
            return;
        }
    }
}
