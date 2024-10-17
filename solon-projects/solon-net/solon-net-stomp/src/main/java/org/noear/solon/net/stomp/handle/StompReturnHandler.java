/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.net.stomp.handle;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;

/**
 * Stomp 返回处理
 *
 * @author noear
 * @since 3.0
 */
public class StompReturnHandler implements ActionReturnHandler {
    static StompReturnHandler instance = new StompReturnHandler();

    public static StompReturnHandler getInstance() {
        return instance;
    }

    /**
     * 匹配
     */
    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        //禁止用于所有返回处理
        return false;
    }

    /**
     * 返回处理
     */
    @Override
    public void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable {
        if (returnValue != null) {
            if (ctx instanceof StompContext) {
                ((StompContext) ctx).commit(returnValue);
            }
        }
    }
}