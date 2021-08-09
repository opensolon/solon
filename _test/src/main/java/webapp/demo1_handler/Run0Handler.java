package webapp.demo1_handler;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 简单的http处理
 * */
@Mapping("/demo1/run0/")
@Controller
public class Run0Handler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        if(ctx.param("str") == null) {
            ctx.output("是null");
        }else{
            ctx.output("不是null(ok)");
        }
    }
}
