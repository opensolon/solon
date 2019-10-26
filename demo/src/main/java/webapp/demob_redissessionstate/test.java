package webapp.demob_redissessionstate;

import org.noear.snack.ONode;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;

import java.util.Date;

@XController
public class test  {
    @XMapping("/demob/setval")
    public void setVal(XContext ctx){
        ctx.sessionSet("v1", new Date());
        ctx.sessionSet("v2","我是字符串");
        ctx.sessionSet("v3", 121212l);
    }

    @XMapping("/demob/getval")
    public void getVal(XContext ctx){
        Object v1 = ctx.session("v1");
        Object v2 = ctx.session("v2");
        Object v3 = ctx.session("v3");
    }
}
