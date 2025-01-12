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
package demo.server;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompSession;
import org.noear.solon.net.stomp.broker.StompBroker;
import org.noear.solon.net.stomp.listener.StompListener;

/**
 * stomp server 必须
 *
 * @author noear
 * @since 2.4
 */
@ServerEndpoint("/chat")
public class ChatStompBroker extends StompBroker implements StompListener {
    public ChatStompBroker() {
        //此为示例，实际按需扩展
        this.addListener(this);
        this.setBrokerDestinationPrefixes("/topic/");
    }

    @Override
    public void onOpen(StompSession session) {
        String user = session.param("user");

        if ("demo".equals(user)) {
            //签权拒绝
            session.close();
        } else {
            //签权通过；并对会话命名
            session.nameAs(user); //命名后，可对 user 发消息
        }
    }

    @Override
    public void onFrame(StompSession session, Frame frame) throws Throwable {
        //System.out.println(frame);
    }

    @Override
    public void onError(StompSession session, Throwable error) {
        //如果出错，反馈给客户端（比如用 "/user/app/errors" 目的地）
        getEmitter().sendToSession(session,
                "/user/app/errors",
                new Message(error.getMessage()));
    }
}