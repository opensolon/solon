package org.noear.solon.extend.sessionstate.redis;

import org.noear.redisx.RedisClient;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

import java.util.Properties;

/**
 * @author noear 2021/2/14 created
 */
public class RedisSessionStateFactory implements SessionStateFactory {
    private static RedisSessionStateFactory instance;
    public static RedisSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new RedisSessionStateFactory();
        }

        return instance;
    }

    private RedisSessionStateFactory() {
        Properties prop = Solon.cfg().getProp("server.session.state.redis");

        if (prop.size() < 4) {
            System.err.println("Error configuration: solon.session.state.redis");
            return;
        }

        redisClient = new RedisClient(prop);
    }

    private RedisClient redisClient;

    protected RedisClient redisClient() {
        return redisClient;
    }

    public static final int SESSION_STATE_PRIORITY = 2;
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }


    @Override
    public SessionState create(Context ctx) {
        return new RedisSessionState(ctx);
    }
}
