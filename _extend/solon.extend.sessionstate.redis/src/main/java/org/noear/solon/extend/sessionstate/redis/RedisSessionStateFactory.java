package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.Solon;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

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
        NvMap map = Solon.cfg().getXmap("server.session.state.redis");

        if (map.size() < 4) {
            System.err.println("Error configuration: solon.session.state.redis");
            return;
        }

        redisX = new RedisX(
                map.get("server"),
                map.get("password"),
                map.getInt("db"),
                map.getInt("maxTotaol"));
    }

    private RedisX redisX;

    protected RedisX getRedisX() {
        return redisX;
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
