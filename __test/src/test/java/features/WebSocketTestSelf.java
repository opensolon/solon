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
package features;

import org.noear.java_websocket.client.SimpleWebSocketClient;
import org.java_websocket.client.WebSocketClient;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demof_websocket.WsDemoClient;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SolonTest(App.class)
public class WebSocketTestSelf {
    @Test
    public void test1() throws Throwable {
        //
        // 测试 jetty or undertow 自带的 websocket
        //
        Thread.sleep(100);
        long time_start = System.currentTimeMillis();

        WsDemoClient client = new WsDemoClient(URI.create("ws://127.0.0.1:8080/demof/websocket/12?u=a&p=1"));
        client.connect();

        while (!client.isOpen()) {
            if (System.currentTimeMillis() - time_start > 1000 * 2) {
                throw new RuntimeException("没有自带WebSocket服务或链接超时");
            }

            Thread.sleep(100);
            //System.out.println("还没有打开:" + client.getReadyState());
        }
        System.out.println("建立websocket连接");
        Exception errors = null;
        try {
            client.send("test0");
            client.send("test1");
            client.send("test2");
            client.send("test3");
        }catch (Exception e){
            errors = e;
        }

        assert errors == null;
        System.out.println("测试完成...");
    }

    @Test
    public void test_async_message3_self_ws() throws Throwable {
        CompletableFuture<Boolean> check = new CompletableFuture<>();
        WebSocketClient webSocketClient = new SimpleWebSocketClient(URI.create("ws://127.0.0.1:8080/demof/websocket/12")){
            @Override
            public void onMessage(String message) {
                System.out.println("异步发送-ws-self::实例监到，收到了：" + message);
                check.complete(true);
            }
        };
        webSocketClient.connectBlocking();

        //异步发
        webSocketClient.send("test0");
        webSocketClient.send("test1");
        webSocketClient.send("test2");
        webSocketClient.send("test3");

        assert check.get(2, TimeUnit.SECONDS);

        webSocketClient.close();

        Thread.sleep(100);
    }
}


