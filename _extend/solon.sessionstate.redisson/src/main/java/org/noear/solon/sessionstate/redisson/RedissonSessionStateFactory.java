package org.noear.solon.sessionstate.redisson;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;
import org.redisson.api.RedissonClient;

import java.util.Properties;

/**
 * @author noear 2021/2/14 created
 */
public class RedissonSessionStateFactory implements SessionStateFactory {
    private static RedissonSessionStateFactory instance;
    public static RedissonSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new RedissonSessionStateFactory();
        }

        return instance;
    }

    private RedissonSessionStateFactory() {
        Properties prop = Solon.cfg().getProp("server.session.state.redis");

        if (prop.size() < 4) {
            System.err.println("Error configuration: solon.session.state.redis");
            return;
        }

        redisClient = RedissonBuilder.build(prop);
    }

    private RedissonClient redisClient;

    protected RedissonClient redisClient() {
        return redisClient;
    }

    public static final int SESSION_STATE_PRIORITY = 2;
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }


    @Override
    public SessionState create(Context ctx) {
        return new RedissonSessionState(ctx);
    }
}
