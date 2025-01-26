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
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

import java.util.Properties;

/**
 * @author noear 2021/2/14 created
 */
public class JedisSessionStateFactory implements SessionStateFactory {
    private static JedisSessionStateFactory instance;
    public static JedisSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new JedisSessionStateFactory();
        }

        return instance;
    }

    private JedisSessionStateFactory() {
        Properties prop = Solon.cfg().getProp("server.session.state.redis");

        if (prop.size() < 4) {
            System.err.println("Error configuration: solon.session.state.redis");
            return;
        }

        redisClient = new RedisClient(prop);
    }

    private RedisClient redisClient;

    public RedisClient redisClient() {
        return redisClient;
    }

    public static final int SESSION_STATE_PRIORITY = 2;
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }


    @Override
    public SessionState create(Context ctx) {
        return new JedisSessionState(ctx);
    }
}
