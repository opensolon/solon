package demo.websocket;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

import java.io.IOException;

/**
 * @author noear 2023/11/13 created
 */
@Configuration
public class WebSocketRouterConfig0 {
    @Bean
    public void init(@Inject WebSocketRouter webSocketRouter){
        //添加前置监听
        webSocketRouter.before(new SimpleWebSocketListener(){
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                super.onMessage(socket, text);
            }
        });
    }
}
