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


import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.StompListener;
import org.noear.solon.net.stomp.StompMessageSender;
import org.noear.solon.net.stomp.StompBroker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * stomp server 必须
 *
 * @author noear
 * @since 2.4
 */
@ServerEndpoint("/chat")
public class ChatStompBroker extends StompBroker {
    //示例，非必须
    private static Timer timer = new Timer();
    //示例，非必须
    private static AtomicInteger atomicInteger = new AtomicInteger();

    @Inject
    StompMessageSender messageSender;

    @Inject("/chat")
    StompMessageSender messageSender1;

    public ChatStompBroker() {
        //此处仅为示例，实际按需扩展，可以不添加
        Solon.context().subBeansOfType(StompListener.class, bean -> {
            this.addListener(bean);
        });

        //this.addListener(new CustomStompListenerImpl());
        //此处仅为服务发送消息示例(继承方式发消息)，可以不添加；间隔3秒发一次
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                messageSender.sendTo("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端1");
                messageSender1.sendTo("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端2");
            }
        }, 3000, 3000);
    }
}