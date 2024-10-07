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

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.annotation.To;
import org.noear.solon.net.stomp.Message;

/**
 * @author noear
 * @since 3.0
 */
public class StompReturnHandler implements ActionReturnHandler {
    static StompReturnHandler instance = new StompReturnHandler();

    public static StompReturnHandler getInstance() {
        return instance;
    }

    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        return ctx.action().method().isAnnotationPresent(To.class);
    }

    @Override
    public void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable {
        if (returnValue != null) {
            if (ctx instanceof StompContext == false) {
                return;
            }

            To anno = action.method().getAnnotation(To.class);

            //sender
            final StompContext ctx1 = (StompContext) ctx;

            //payload
            final Message message;
            if (returnValue instanceof Message) {
                message = (Message) returnValue;
            } else if (returnValue instanceof String) {
                message = new Message((String) returnValue);
            } else {
                message = new Message(ctx.renderAndReturn(returnValue));
            }

            //send-to
            if (anno == null) {
                ctx1.getSender().sendTo(ctx.path(), message);
            } else {
                for (String destination : anno.value()) {
                    if (Utils.isEmpty(destination)) {
                        //如果是空的
                        ctx1.getSender().sendTo(ctx.path(), message);
                    } else {
                        ctx1.getSender().sendTo(destination, message);
                    }
                }
            }
        }
    }
}