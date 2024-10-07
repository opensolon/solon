package demo.server;

import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.To;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2024/10/7 created
 */
@Controller
public class ChatStompController {
    @Mapping("/topic/todoTask1/open")
    //@To("/topic/todoTask1/s1")
    public String test(Context ctx, @Body String text) {
        System.out.println(ctx.headerMap());
        System.out.println(text);
        return "收到：" +text;
    }

    @Mapping("/")
    public void home(Context ctx) {
        ctx.forward("/index.html");
    }
}
