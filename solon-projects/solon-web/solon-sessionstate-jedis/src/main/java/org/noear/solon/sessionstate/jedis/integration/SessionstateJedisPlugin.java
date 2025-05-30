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
package org.noear.solon.sessionstate.jedis.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.sessionstate.jedis.JedisSessionStateFactory;

public class SessionstateJedisPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        if (context.app().enableSessionState() == false) {
            return;
        }

        if (context.app().chainManager().getSessionStateFactory().priority()
                >= JedisSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }

        /*
         *
         * server.session.state.redis:
         * server:
         * password:
         * db: 31
         * maxTotal: 200
         *
         * */

        if (JedisSessionStateFactory.getInstance().redisClient() == null) {
            return;
        }

        context.app().chainManager().setSessionStateFactory(JedisSessionStateFactory.getInstance());

        LogUtil.global().info("Session: Redis session state plugin is loaded");
    }
}
