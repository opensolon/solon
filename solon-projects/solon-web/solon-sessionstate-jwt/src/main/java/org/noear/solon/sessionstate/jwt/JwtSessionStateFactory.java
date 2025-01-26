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
package org.noear.solon.sessionstate.jwt;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionStateFactory implements SessionStateFactory {
    private static JwtSessionStateFactory instance;

    public static JwtSessionStateFactory getInstance() {
        if (instance == null) {
            instance = new JwtSessionStateFactory();
        }

        return instance;
    }

    private JwtSessionStateFactory() {
        String signKey0 = JwtSessionProps.getInstance().secret;
        if (Utils.isNotEmpty(signKey0)) {
            signKey = signKey0;
        }
    }


    private String signKey = "DHPjbM5QczZ2cysd4gpDbG/4SnuwzWX3sA1i6AXiAbo=";

    /**
     * 获取签名Key
     * */
    public String signKey() {
        return signKey;
    }

    /**
     * 设置签名Key
     * */
    public void signKeySet(String key) {
        signKey = key;
    }


    public static final int SESSION_STATE_PRIORITY = 2;

    /**
     * 优先级
     * */
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }


    /**
     * 创建会话状态
     * */
    @Override
    public SessionState create(Context ctx) {
        return new JwtSessionState(ctx);
    }
}
