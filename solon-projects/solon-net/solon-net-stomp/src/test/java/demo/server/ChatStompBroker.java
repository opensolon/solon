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
package demo.server;

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.broker.StompBroker;
import org.noear.solon.net.stomp.handle.ToHandlerStompListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * stomp server 必须
 *
 * @author noear
 * @since 2.4
 */
@ServerEndpoint("/chat")
public class ChatStompBroker extends StompBroker {
    public ChatStompBroker() {
        //此为示例，实际按需扩展
        this.addServerListener(0, new ChatStompListenerImpl());
        this.addServerListener(new ToHandlerStompListener(this));

        //此为示例
        final AtomicInteger atomicInteger = new AtomicInteger();
        RunUtil.scheduleAtFixedRate(() -> {
            getServerEmitter().sendTo("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端1");
        }, 3000, 3000);
    }
}