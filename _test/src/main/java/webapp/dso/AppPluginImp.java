package webapp.dso;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/8/28 created
 */
public class AppPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //Aop.context().beanAroundAdd();
    }
}
