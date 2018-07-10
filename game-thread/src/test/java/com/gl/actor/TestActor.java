package com.gl.actor;

public class TestActor {
    public static void main(String[] args) {
        ActorDispatcher actorDispatcher = new ActorDispatcher("test", 3);
        actorDispatcher.start();
        actorDispatcher.put(1, new IRunner() {
            @Override
            public Object run() {
                System.out.println("IRunner");
                return null;
            }
        });
        actorDispatcher.put(0, new IRunner() {
            @Override
            public Object run() {
                System.out.println("IRunner");
                return 2;
            }
        }, new ICallback() {
            @Override
            public void onResult(Object result) {
                System.out.println("ICallback:" + result.toString());
            }
        }, actorDispatcher.getActor(2));
    }
}
