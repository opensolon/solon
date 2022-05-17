package webapp.demo4_plug_bean;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

@Component
public class Demo4Plug implements Plugin {
    @Override
    public void start(AopContext context) {
        Solon.global().get("/demo4/*", c -> c.output("是插件生出了我..."));
    }
}
