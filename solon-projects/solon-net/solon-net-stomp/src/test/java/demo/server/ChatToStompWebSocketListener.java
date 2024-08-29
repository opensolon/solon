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
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.ToStompWebSocketListener;
import org.noear.solon.net.stomp.impl.StompMessageSendingTemplate;

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
public class ChatToStompWebSocketListener extends ToStompWebSocketListener {

    //示例，非必须
    private static Timer timer = new Timer();
    //示例，非必须
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public ChatToStompWebSocketListener() {
        super();
        //此处仅为示例，实际按需扩展，可以不添加
        Solon.context().getBeanAsync(CustomStompListenerImpl.class, bean -> {
            this.addListener(bean);
        });
        //this.addListener(new CustomStompListenerImpl());
        //此处仅为服务发送消息示例(继承方式发消息)，可以不添加；间隔3秒发一次
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                stompMessageSendingTemplate.send("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端1");
                StompMessageSendingTemplate stompMessageSendingTemplate = Solon.context().getBean("/chat");
                stompMessageSendingTemplate.send("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端2");
            }
        }, 3000, 3000);
        //此处仅为服务发送消息示例(异步获取messageTemp)，可以不添加；间隔3秒发一次
        Solon.context().getBeanAsync("/chat", stompMessageSendingTemplate -> {
            //方式一
            //@Inject("/chat") StompMessageSendingTemplate stompMessageSendingTemplate;
            //方式二
            //StompMessageSendingTemplate stompMessageSendingTemplate = Solon.context().getBean("/chat");
            StompMessageSendingTemplate messageSendingTemplate = (StompMessageSendingTemplate) stompMessageSendingTemplate;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    messageSendingTemplate.send("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端2");
                }
            }, 3000, 3000);
        });
    }
}