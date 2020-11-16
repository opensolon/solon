package webapp.demo4_plug_bean;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.Plugin;

@Component
public class Demo4Plug implements Plugin {
    @Override
    public void start(Solon app) {
        app.get("/demo4/*", c -> c.output("是插件生出了我..."));
    }
}
