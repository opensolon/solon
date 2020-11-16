package webapp.dao;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.core.Plugin;
import webapp.demo2_mvc.MappingController;
import webapp.demo2_mvc.ParamController;

@Bean
public class TestExt implements Plugin {
    @Override
    public void start(Solon app) {
        app.add("/demo2x/param", ParamController.class);
        app.add("/demo2x/mapping", MappingController.class);

        app.get("/",c->c.redirect("/debug.htm"));


        app.plug(new Plugin() {
            @Override
            public void start(Solon app) {

            }

            @Override
            public void stop() throws Throwable {
                System.out.println("通知你一下，我现在要停了");
            }
        });
    }
}
