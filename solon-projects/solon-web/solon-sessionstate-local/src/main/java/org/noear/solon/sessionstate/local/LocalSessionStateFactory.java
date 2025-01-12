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
package org.noear.solon.sessionstate.local;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

/**
 * @author noear 2021/2/14 created
 */
public class LocalSessionStateFactory implements SessionStateFactory {
    private static LocalSessionStateFactory instance;
    public static LocalSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new LocalSessionStateFactory();
        }

        return instance;
    }
    private LocalSessionStateFactory(){

    }

    public static final int SESSION_STATE_PRIORITY = 1;
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }

    @Override
    public SessionState create(Context ctx) {
        return new LocalSessionState(ctx);
    }

}
