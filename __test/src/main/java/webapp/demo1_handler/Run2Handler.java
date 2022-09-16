package webapp.demo1_handler;

import org.noear.solon.core.handle.Gateway;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;

/**
 * 简单的http处理(带简单的内部导航 + 前后置处理)
 * */
@Mapping("/demo1/run2/*")
@Controller
public class Run2Handler extends Gateway {
    @Override
    protected void register() {
        before(c->{if(false){}});

        add("send", (c)->{c.output(c.url());});
        add("test", (c)->{c.output(c.url());});
        add("dock", (c)->{c.output(c.url());});
        add("ip", (c)->{c.output(c.ip());});

        after(c->{});
    }
}
