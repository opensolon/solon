package demo;

import org.noear.solon.Solon;
import org.noear.solon.extend.shiro.ShiroPluginImp;

/**
 * @author noear 2021/5/13 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            app.pluginAdd(0, new ShiroPluginImp());
        });
    }
}
