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
package demo;


import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.okhttps.WHttpTask;
import com.ejlchina.stomp.Commands;
import com.ejlchina.stomp.Header;
import com.ejlchina.stomp.Message;
import com.ejlchina.stomp.Stomp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * stomp client 测试
 *
 * @author limliu
 * @since 2.7
 */
public class StompClientTest {

    static final Logger log = LoggerFactory.getLogger(StompClientTest.class);


    public static void main(String[] args) throws Exception {
        WHttpTask whttpTask = OkHttps.webSocket("ws://127.0.0.1:8080/chat?user=test001")
                .heatbeat(5, 5);

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("resource", "test"));
        AtomicInteger atomicInteger = new AtomicInteger();
        conn(whttpTask, headers, 1, (stompSession) -> {
            //接收消息
            stompSession.subscribe("/topic/todoTask1/*", new ArrayList<>(), msg -> {
                log.info("{}", msg);
            });
            //客户端发消息给服务端
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Message message = transform(Commands.SEND, "/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自客户端");
                    stompSession.send(message);
                }
            }, 2000, 2000);
        });
    }

    private static AtomicReference<Stomp> stompAtomicReference = new AtomicReference<>();

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private static Timer timer = new Timer();

    private static void conn(WHttpTask whttpTask, List<Header> headers, int count, Consumer<Stomp> onConnected) {
        log.info("Init webSocket client index {}", count);
        Stomp.over(whttpTask).setOnConnected(stomp -> {
            log.info("webSocket 连接成功");
            stompAtomicReference.set(stomp);
            atomicInteger.set(0);
            onConnected.accept(stomp);
        }).setOnDisconnected(c -> {
            log.info("webSocket 连接已断开 {}", c.toString());
            stompAtomicReference.set(null);
            //延迟重连，最大延迟30秒；连接成功后重置该值
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //断线重连
                    conn(whttpTask, headers, count + 1, onConnected);
                }
            }, (atomicInteger.get() < 31 ? atomicInteger.incrementAndGet() : atomicInteger.get()) * 1000);
        }).setOnError(msg -> {
            log.info("webSocket OnError {}", msg.getPayload());
        }).setOnException(e -> {
            log.info("webSocket OnException {}", e.getMessage());
        }).connect(headers);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @return
     */
    public static Message transform(String command, String destination, String payload) {
        return transform(command, destination, payload, null, null);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @param contentType
     * @return
     */
    public static Message transform(String command, String destination, String payload, String contentType) {
        return transform(command, destination, payload, contentType, null);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @param headers
     * @return
     */
    public static Message transform(String command, String destination, String payload, List<Header> headers) {
        return transform(command, destination, payload, null, headers);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @param contentType
     * @param headers
     * @return
     */
    public static Message transform(String command, String destination, String payload, String contentType, List<Header> headers) {
        Message message = new Message(command, new ArrayList<>(), payload);
        if (contentType != null && contentType.length() > 0) {
            message.getHeaders().add(new Header(Header.CONTENT_TYPE, contentType));
        }
        message.getHeaders().add(new Header(Header.DESTINATION, destination));
        if (headers != null && headers.size() > 0) {
            message.getHeaders().addAll(headers);
        }
        return message;
    }
}