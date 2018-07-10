package com.gal.group.redisson;

import com.gl.group.redisson.RedissonMannager;
import org.redisson.api.RBatch;

public class TestRedisson {

    public static void main(String[] args) {
        RedissonMannager.connectRedis("D:/intellijw/java/gamemodel/game.redis/src/main/resources");
        RBatch batch = RedissonMannager.getRedisssonClient().createBatch();
        batch.getSet("redisson").readAllAsync();
        System.out.print(batch.execute());
    }
}
