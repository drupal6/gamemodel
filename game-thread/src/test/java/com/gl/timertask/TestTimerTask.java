package com.gl.timertask;

import com.gl.timertask.task.TaskThread;
import com.gl.timertask.timer.event.Event;

import java.time.LocalTime;

public class TestTimerTask {

    public static void main(String[] args) throws InterruptedException {
        TaskThread taskThread = new TaskThread(Thread.currentThread().getThreadGroup(), "test", 1000);
        taskThread.addTimeEvent(new TimerEvent1(0));
        taskThread.start();
        taskThread.executor(new TestTask());
        for(int i = 0; i < 1000; i++) {
            Thread.sleep(1000l);
            taskThread.executor(new TestTask());
        }
    }

    static class TestTask implements Runnable {
        @Override
        public void run() {
            System.out.println("run task");
        }
    }

    static class TimerEvent1 extends Event {

        protected int hour = -1;
        protected int min = -1;
        protected int sec = -1;

        public TimerEvent1(long end) {
            super(end);
        }

        @Override
        public void run() {
            LocalTime localTime = LocalTime.now();
            int _sec = localTime.getSecond();
            if (sec != _sec) { // 每秒钟执行
                sec = _sec;
                System.out.println("timer event run per sec.");
            }
            int _min = localTime.getMinute();
            if (min != _min) { // 每分钟执行
                min = _min;
                System.out.println("timer event run per min.");
            }
            int _hour = localTime.getHour();
            if (hour != _hour) { // 每小时执行
                hour = _hour;
                System.out.println("timer event run per hour.");
            }
        }
    }
}
