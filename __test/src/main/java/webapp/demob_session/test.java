package webapp.demob_session;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import java.util.Date;

@Controller
public class test {
    @Mapping("/demob/session/setval")
    public void setVal(Context ctx) {
        ctx.sessionSet("v1", new Date());
        ctx.sessionSet("v2", "我是字符串");
        ctx.sessionSet("v3", 121212l);
    }

    @Mapping(value = "/demob/session/getval", produces = "text/html;charset=utf-8")
    public void getVal(Context ctx) {
        Object v1 = ctx.session("v1");
        Object v2 = ctx.session("v2");
        Object v3 = ctx.session("v3");

        ctx.output(v1 + "<br/>");
        ctx.output(v2 + "<br/>");
        ctx.output(v3 + "<br/>");
    }
}
