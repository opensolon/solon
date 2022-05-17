package webapp.dso;

import freemarker.template.Configuration;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/8/28 created
 */
public class AppPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //Aop.context().beanAroundAdd();

        app.onEvent(Configuration.class, cfg -> {
            System.out.println("%%%%%%%%%%%%%%%%%%");
            cfg.setSetting("classic_compatible", "true");
        });
    }
}
