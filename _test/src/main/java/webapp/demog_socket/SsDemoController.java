package webapp.demog_socket;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Controller
public class SsDemoController {

    @Mapping(value = "/demog/**", method = MethodType.SOCKET)
    public void test(Context ctx) throws Exception {
        if (ctx == null) {
            return;
        }

        String msg = ctx.body();

        if (msg.equals("close")) {
            ctx.output("它叫我关了：" + msg);
            System.out.println("它叫我关了：" + msg + "!!!");
            ctx.close();//关掉
        } else {
            ctx.output("我收到了：" + msg);
        }
    }
}

