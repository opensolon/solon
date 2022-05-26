package webapp.dso;

import freemarker.template.Configuration;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/8/28 created
 */
public class AppPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //Aop.context().beanAroundAdd();

        Solon.app().onEvent(Configuration.class, cfg -> {
            System.out.println("%%%%%%%%%%%%%%%%%%");
            cfg.setSetting("classic_compatible", "true");
        });
    }
}
