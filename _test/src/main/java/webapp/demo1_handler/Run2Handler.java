package webapp.demo1_handler;

import org.noear.solon.XNav;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;

/**
 * 简单的http处理(带简单的内部导航 + 前后置处理)
 * */
@XMapping("/demo1/run2/*")
@XController
public class Run2Handler extends XNav {
    public Run2Handler() {
        before(c->{if(false){}});

        add("send", (c)->{c.output(c.path());});
        add("test", (c)->{c.output(c.path());});
        add("dock", (c)->{c.output(c.path());});

        after(c->{});
    }
}
