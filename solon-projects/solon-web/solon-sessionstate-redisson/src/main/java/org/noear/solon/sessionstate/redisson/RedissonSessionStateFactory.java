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

        if (prop.size() < 1) {
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
