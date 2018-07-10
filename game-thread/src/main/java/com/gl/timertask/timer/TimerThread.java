package com.gl.timertask.timer;

import com.gl.timertask.task.TaskThread;
import com.gl.timertask.timer.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TimerThread extends Timer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerThread.class);

    private TaskThread taskThread;

    private Collection<Event> events;

    private String prefix;

    private TimerTask task;

    public TimerThread(TaskThread taskThread, String prefix) {
        super("TimerThread-" + prefix);
        this.prefix = prefix;
        this.taskThread = taskThread;
        this.events = Collections.synchronizedList(new ArrayList<Event>());
    }

    public void start() {
        task = new TimerTask() {
            @Override
            public void run() {
                synchronized (events) {
                    Iterator<Event> it = events.iterator();
                    while (it.hasNext()) {
                        Event event = it.next();
                        if(event.remain() >= 0) {
                            if(event.getLoop() > 0) {
                                event.setLoop(event.getLoop() - 1);
                                TimerThread.this.taskThread.executor(event);
                            } else if(event.getLoop() < 0) {
                                TimerThread.this.taskThread.executor(event);
                            }
                        } else {
                            TimerThread.this.taskThread.executor(event);
                            event.setLoop(0);
                        }
                        if(event.getLoop() == 0) {
                            it.remove();
                        }
                    }
                }
            }
        };
        schedule(task, 0, taskThread.getHeart());
    }

    public void stop(boolean flag) {
        synchronized (events) {
            events.clear();
            if(task != null) {
                task.cancel();
            }
            cancel();
        }
    }

    public void addEvent(Event event) {
        synchronized (events) {
            events.add(event);
        }
    }

    public void removeEvent(Event event) {
        synchronized (events) {
            events.remove(event);
        }
    }
}
