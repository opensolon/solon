package io.lettuce.solon;

import io.lettuce.core.RedisClient;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class DemoService {
    @Inject
    RedisClient redisClient;

    public void demoSet() {
        redisClient.connect().sync().set("test", "test");
        System.out.println(redisClient.connect().sync().get("test"));
        redisClient.connect().sync().del("test");
    }
}
