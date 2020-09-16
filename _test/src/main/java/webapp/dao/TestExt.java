package webapp.dao;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XBean;
import org.noear.solon.core.XAction;
import org.noear.solon.core.XPlugin;
import webapp.demo2_mvc.MappingController;
import webapp.demo2_mvc.ParamController;

@XBean
public class TestExt implements XPlugin {
    @Override
    public void start(XApp app) {
        app.add("/demo2x/param", ParamController.class);
        app.add("/demo2x/mapping", MappingController.class);

        app.get("/",c->c.redirect("/debug.htm"));

        app.before("@@",x -> {
            XAction action = x.action();
            if(action != null){
                return;
            }
        });

        app.plug(new XPlugin() {
            @Override
            public void start(XApp app) {

            }

            @Override
            public void stop() throws Throwable {
                System.out.println("通知你一下，我现在要停了");
            }
        });
    }
}
