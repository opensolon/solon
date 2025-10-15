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
package features.socketd;

import org.java_websocket.client.WebSocketClient;
import org.junit.jupiter.api.Test;
import org.noear.java_websocket.client.SimpleWebSocketClient;
import org.noear.snack4.ONode;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author noear 2022/3/11 created
 */
@SolonTest(App.class)
public class SocketAsyncTest {
    @Test
    public void test_async_message() throws Throwable {
        int _port = 8080 + 20000;

        CompletableFuture<Boolean> check = new CompletableFuture<>();

        ClientSession session = SocketD.createClient("tcp://localhost:" + _port)
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("异步发送::实例监到，收到了：" + message);
                        check.complete(true);
                    }
                })
                .openOrThow();


        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.ofBean(map).toJson();

        //异步发
        session.send(root + "/demoh/rpc/hello", new StringEntity(map_josn)
                .metaPut("Content-Type", "application/json"));

        assert check.get(2, TimeUnit.SECONDS);
    }

    @Test
    public void test_async_message2() throws Throwable {

        CompletableFuture<Boolean> check = new CompletableFuture<>();

        WebSocketClient webSocketClient = new SimpleWebSocketClient(URI.create("ws://127.0.0.1:18080/demof/websocket/12")){
            @Override
            public void onMessage(String message) {
                System.out.println("异步发送-ws::实例监到，收到了：" + message);
                check.complete(true);
            }
        };
        webSocketClient.connectBlocking();

        //异步发
        webSocketClient.send("test");

        assert check.get(2, TimeUnit.SECONDS);
    }


}
