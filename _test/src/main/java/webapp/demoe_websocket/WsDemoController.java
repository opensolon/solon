package webapp.demoe_websocket;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Http;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.WebSocket;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;

@Controller
public class WsDemoController {
    @WebSocket
    @Mapping("/demoe/*")
    public void test(Context ctx) throws Exception{
        if(ctx == null){
            return;
        }

        String msg = ctx.body();

        if(msg.equals("close")){
            ctx.output("它叫我关了："+ msg);
            System.out.println("它叫我关了："+ msg+"!!!");
            ctx.close();//关掉
        }else{
            ctx.output("我收到了："+ msg);
        }
    }

    @Http
    @Mapping("/demoe/websocket")
    public Object test_client(Context ctx){
        ModelAndView mv = new ModelAndView("demoe/websocket.ftl");
        mv.put("app_port", Solon.global().port() + 10000);

        return mv;
    }
}
