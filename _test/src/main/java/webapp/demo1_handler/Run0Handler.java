package webapp.demo1_handler;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;

/**
 * 简单的http处理
 * */
@Mapping("/demo1/run0/")
@Controller
public class Run0Handler implements Handler {
    @Override
    public void handle(Context cxt) throws Exception {
        if(cxt.param("str") == null) {
            cxt.output("是null");
        }else{
            cxt.output("不是null(ok)");
        }
    }
}
