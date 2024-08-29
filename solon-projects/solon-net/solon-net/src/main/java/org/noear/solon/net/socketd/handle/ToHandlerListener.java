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
package org.noear.solon.net.socketd.handle;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 转到 Handler 接口协议的 Listener（服务端、客户端，都可用）
 *
 * @author noear
 * @since 2.0
 */
public class ToHandlerListener extends EventListener {
    private static final Logger log = LoggerFactory.getLogger(ToHandlerListener.class);

    public ToHandlerListener() {
        super();
        doOnMessage(this::onMessageDo);
    }

    protected void onMessageDo(Session session, Message message) throws IOException {
        if (Utils.isEmpty(message.event())) {
            log.warn("This message is missing route, sid={}", message.sid());
            return;
        }

        try {
            SocketdContext ctx = new SocketdContext(session, message);

            Solon.app().tryHandle(ctx);

            if (ctx.getHandled() || ctx.status() != 404) {
                ctx.commit();
            } else {
                session.sendAlarm(message, "No event handler was found! like code=404");
            }
        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            log.warn(e.getMessage(), e);

            if (session.isValid()) {
                session.sendAlarm(message, e.getMessage());
            }
        }
    }
}
