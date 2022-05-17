package webapp.dao;

import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.Plugin;
import webapp.demo2_mvc.MappingController;
import webapp.demo2_mvc.ParamController;

@Component
public class TestExt implements Plugin {
    @Override
    public void start(AopContext context) {
        app.add("/demo2x/param", ParamController.class);
        app.add("/demo2x/mapping", MappingController.class);

        app.get("/",c->c.forward("/debug.htm"));


        app.plug(new Plugin() {
            @Override
            public void start(AopContext context) {

            }

            @Override
            public void stop() throws Throwable {
                System.out.println("通知你一下，我现在要停了");
            }
        });
    }
}
