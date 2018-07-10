package com.gl.actor;

import com.gl.actor.impl.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorDispatcher {

    private final Logger LOGGER = LoggerFactory.getLogger(ActorDispatcher.class);

    private final Actor[] actors;

    public ActorDispatcher(String name, int poolSize) {
        if(poolSize < 0) {
            throw new  IllegalArgumentException();
        }
        actors = new Actor[poolSize];
        for(int i = 0; i < poolSize; i++) {
            actors[i] = new Actor("ActorDispatcher-" + name + "-i", 0);
        }
    }

    public ActorDispatcher(String name, int poolSize, int capacity) {
        if(poolSize < 0) {
            throw new  IllegalArgumentException();
        }
        actors = new Actor[poolSize];
        for(int i = 0; i < poolSize; i++) {
            actors[i] = new Actor("ActorDispatcher-" + name + "-i", capacity);
        }
    }

    public IActor getActor(int id) {
        int idx = Math.abs(id) % actors.length;
        return actors[idx];
    }

    public void put(int dispatcherId, IRunner runner) {
        getActor(dispatcherId).put(runner);
    }

    public void put(int dispatcherId, IRunner runner, ICallback callback, IActor actor) {
        getActor(dispatcherId).put(runner, callback, actor);
    }

    public boolean start() {
        for(int i = 0; i < actors.length; i++) {
            if(false == actors[i].start()) {
                return false;
            }
        }
        return true;
    }

    public void stop() {
        for(int i = 0; i < actors.length; i++) {
           actors[i].stop();
        }
    }

    public void stopWhenEmpty() {
        for(int i = 0; i < actors.length; i++) {
            actors[i].stopWhenEmpty();;
        }
    }

    public void waiForStop() {
        while (isRunning()) {
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
        }
    }
    public boolean isStopping() {
        for(int i = 0; i < actors.length; i++) {
            if(actors[i].isRunning()) {
                return false;
            }
        }
        return true;
    }

    public boolean isRunning() {
        for(int i = 0; i < actors.length; i++) {
            if(false == actors[i].isRunning()) {
                return false;
            }
        }
        return true;
    }
}
