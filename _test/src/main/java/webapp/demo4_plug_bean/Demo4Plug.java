package webapp.demo4_plug_bean;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XBean;
import org.noear.solon.core.XPlugin;

@XBean
public class Demo4Plug implements XPlugin {
    @Override
    public void start(XApp app) {
        app.get("/demo4/*", c -> c.output("是插件生出了我..."));
    }
}
