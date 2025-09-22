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
package org.noear.solon.sessionstate.jwt.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.sessionstate.jwt.JwtSessionStateFactory;
import org.noear.solon.sessionstate.jwt.JwtSessionProps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class SessionstateJwtPlugin implements Plugin {
    static final Logger log = LoggerFactory.getLogger(SessionstateJwtPlugin.class);

    @Override
    public void start(AppContext context) {
        if (context.app().enableSessionState() == false) {
            return;
        }

        if (context.app().chains().getSessionStateFactory().priority()
                >= JwtSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }

        JwtSessionProps.getInstance();

        context.app().chains().setSessionStateFactory(JwtSessionStateFactory.getInstance());

        if (log.isDebugEnabled()) {
            log.debug("Session: Jwt session state plugin is loaded");
        }
    }
}
