package com.gl.task;

public class TestTaskQueueThread {
    public static void main(String[] args) throws InterruptedException {
        TaskQueueThread tqt = new TaskQueueThread(Thread.currentThread().getThreadGroup(), "test");
        tqt.start();
        ITask t = new TestTask();
        tqt.addTask(t);
        Thread.sleep(5000);
        tqt.addTask(t);
    }

    static class TestTask extends ITask {

        @Override
        public void run() {
            System.out.println("TestTask run");
        }
    }
}
