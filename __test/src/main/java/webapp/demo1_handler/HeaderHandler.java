package webapp.demo1_handler;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.ModelAndView;

import java.nio.charset.StandardCharsets;

/**
 * 实现简单的 mvc 效果
 * */
@Mapping("/demo1/header/*")
@Controller
public class HeaderHandler implements Handler {
    @Override
    public void handle(Context cxt) throws Throwable {
        byte[] bytes = "Hello world!".getBytes(StandardCharsets.UTF_8);
        cxt.contentLength(bytes.length);
        cxt.output(bytes);
    }
}
