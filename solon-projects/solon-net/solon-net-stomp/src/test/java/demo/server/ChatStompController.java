package demo.server;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.net.stomp.StompEmitter;

/**
 * @author noear 2024/10/7 created
 */
@Controller
public class ChatStompController {
    @Inject //@Inject("/chat")
    StompEmitter stompSender;

    @Mapping("/topic/todoTask1/open")
    //@To("/topic/todoTask1/s1")
    public String test(Context ctx, @Body String text) {
        System.out.println(ctx.headerMap());
        System.out.println(text);

        stompSender.sendTo(ctx.path(), "收到1：" +text);

        return "收到2：" +text;
    }

    @Mapping("/")
    public void home(Context ctx) {
        ctx.forward("/index.html");
    }
}
