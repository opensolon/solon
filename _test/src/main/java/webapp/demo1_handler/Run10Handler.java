package webapp.demo1_handler;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 简单的http处理
 * */
@Mapping("/demo1/run10/*")
@Controller
public class Run10Handler implements Handler {
    @Override
    public void handle(Context cxt) throws Exception {
        cxt.forward("/demo1/run11/a");
    }
}
