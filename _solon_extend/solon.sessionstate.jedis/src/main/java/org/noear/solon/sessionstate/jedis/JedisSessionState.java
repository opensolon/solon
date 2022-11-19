package org.noear.solon.sessionstate.jedis;

import org.noear.redisx.RedisClient;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.SessionStateBase;
import org.noear.solon.core.handle.Context;
import org.noear.solon.data.cache.Serializer;

import java.util.Collection;

/**
 * 它会是个单例，不能有上下文数据
 * */
public class JedisSessionState extends SessionStateBase {

    private RedisClient redisClient;
    private Serializer<String> serializer;

    protected JedisSessionState(Context ctx) {
        super(ctx);
        this.serializer = JavabinSerializer.instance;
        this.redisClient = JedisSessionStateFactory.getInstance().redisClient();
    }

    //
    // session control
    //

    @Override
    public String sessionId() {
        String _sessionId = ctx.attr("sessionId", null);

        if (_sessionId == null) {
            _sessionId = sessionIdGet(false);
            ctx.attrSet("sessionId", _sessionId);
        }

        return _sessionId;
    }

    @Override
    public String sessionChangeId() {
        sessionIdGet(true);
        ctx.attrSet("sessionId", null);
        return sessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return redisClient.openAndGet((ru) -> ru.key(sessionId()).hashGetAllKeys());
    }


    @Override
    public Object sessionGet(String key) {
        String val = redisClient.openAndGet((ru) -> ru.key(sessionId()).expire(_expiry).hashGet(key));

        if (val == null) {
            return null;
        }

        try {
            return serializer.deserialize(val);
        } catch (Exception e) {
            throw new RuntimeException("Session state deserialization error: " + key + " = " + val, e);
        }
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            try {
                String json = serializer.serialize(val);

                redisClient.open((ru) -> ru.key(sessionId()).expire(_expiry).hashSet(key, json));
            } catch (Exception e) {
                throw new RuntimeException("Session state serialization error: " + key + " = " + val, e);
            }
        }
    }

    @Override
    public void sessionRemove(String key) {
        redisClient.open((ru) -> ru.key(sessionId()).expire(_expiry).hashDel(key));
    }

    @Override
    public void sessionClear() {
        redisClient.open((ru) -> ru.key(sessionId()).delete());
    }

    @Override
    public void sessionReset() {
        sessionClear();
        sessionChangeId();
    }

    @Override
    public void sessionRefresh() {
        String sid = sessionIdPush();

        if (Utils.isEmpty(sid) == false) {
            redisClient.open((ru) -> ru.key(sessionId()).expire(_expiry).delay());
        }
    }


    @Override
    public boolean replaceable() {
        return false;
    }
}
