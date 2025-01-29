package demo.server;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.net.stomp.StompEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/10/7 created
 */
@Controller
public class ChatStompController {
    @Inject //@Inject("/chat")
    StompEmitter stompEmitter;

    @Message
    @Mapping("/topic/todoTask1/open")
    //@To("*:/topic/todoTask1/s1")
    public Map<String, Object> test(Context ctx, @Body String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", text);
        map.put("type", "收到2");

        return map;
    }

    @Message
    @Mapping("/app/todoTask1/user")
    @To("${user}:/topic/todoTask1/s1")
    public Map<String, Object> app_user(Context ctx, @Header("user") String user, @Body String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", text);
        map.put("type", "收到2");

        return map;
    }

//    @Message
//    @Mapping("/app/todoTask1/self")
//    //@To(".:/topic/todoTask1/s1")
//    public Publisher<Map<String, Object>> app_self(Context ctx, @Body String text) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("data", text);
//        map.put("type", "收到2");
//
//        return Completable.just(map);
//    }

    @Message
    @Mapping("/app/todoTask1/error")
    @To(".:/topic/todoTask1/s1")
    public void app_error(@Body String text) {
        throw new IllegalArgumentException("错误测试 - " + text);
    }

    @Mapping("/")
    public void home(Context ctx) {
        ctx.forward("/index.html");
    }
}