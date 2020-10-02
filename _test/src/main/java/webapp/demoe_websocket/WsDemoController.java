package webapp.demoe_websocket;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMethod;

@XController
public class WsDemoController {
    @XMapping(value = "/demoe/*",method = XMethod.WEBSOCKET)
    public void test(XContext ctx) throws Exception{
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

    @XMapping(value = "/demoe/websocket")
    public Object test_client(XContext ctx){
        ModelAndView mv = new ModelAndView("demoe/websocket.ftl");
        mv.put("app_port", XApp.global().port() + 10000);

        return mv;
    }
}
