package webapp.dao;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import webapp.demo2_mvc.MappingController;
import webapp.demo2_mvc.ParamController;

@Component
public class TestExt implements Plugin {
    @Override
    public void start(AopContext context) {
        Solon.global().add("/demo2x/param", ParamController.class);
        Solon.global().add("/demo2x/mapping", MappingController.class);

        Solon.global().get("/",c->c.forward("/debug.htm"));


        Solon.global().plug(new Plugin() {
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
