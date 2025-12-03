package features.undertow.ws;

import org.java_websocket.client.WebSocketClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.noear.java_websocket.client.SimpleWebSocketClient;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonTest;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SolonTest(ServerApp.class)
public class ClientTest {
    @Test
    public void test_async_message3_self_ws() throws Throwable {
        CompletableFuture<Boolean> check = new CompletableFuture<>();
        WebSocketClient webSocketClient = new SimpleWebSocketClient(URI.create("ws://localhost:8080/demo")){
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

    @AfterAll
    public static void allAft() {
        //避免与同项目的，边上的 app 冲突
        if (Solon.app() != null) {
            Solon.stopBlock();
        }
    }
}
