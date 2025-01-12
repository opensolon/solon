/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.sessionstate.jedis;

import org.noear.redisx.RedisClient;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.web.SessionStateBase;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.serialize.Serializer;

import java.io.IOException;
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

    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public long creationTime() {
        return ctx.sessionAsLong(ServerConstants.SESSION_CREATION_TIME, 0L);
    }

    @Override
    public long lastAccessTime() {
        return ctx.sessionAsLong(ServerConstants.SESSION_LAST_ACCESS_TIME, 0L);
    }

    //
    // session control
    //

    private String sessionId;

    @Override
    public String sessionId() {
        if (sessionId == null) {
            sessionId = sessionIdGet(false);
        }
        return sessionId;
    }

    @Override
    public String sessionChangeId() {
        return sessionId = sessionIdGet(true);
    }

    @Override
    public Collection<String> sessionKeys() {
        return redisClient.openAndGet((ru) -> ru.key(sessionId()).hashGetAllKeys());
    }


    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        String val = redisClient.openAndGet((ru) -> ru.key(sessionId()).expire(_expiry).hashGet(key));

        if (val == null) {
            return null;
        }

        try {
            return (T) serializer.deserialize(val, clz);
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
    public void sessionRefresh() throws IOException {
        String sid = sessionIdPush();

        if (Utils.isEmpty(sid) == false) {
            long now = System.currentTimeMillis();

            String json = serializer.serialize(now);
            redisClient.open((ru) -> ru.key(sessionId()).expire(_expiry).hashInit(ServerConstants.SESSION_CREATION_TIME, json));
        }
    }

    @Override
    public void sessionPublish() throws IOException {
        String sid = sessionId();

        if (Utils.isEmpty(sid) == false) {
            long now = System.currentTimeMillis();

            String json = serializer.serialize(now);
            redisClient.open((ru) -> ru.key(sessionId()).expire(_expiry).hashSet(ServerConstants.SESSION_LAST_ACCESS_TIME, json));
        }
    }
}
