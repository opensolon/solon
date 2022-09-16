package webapp;

import org.noear.solon.Solon;
import org.noear.solon.hotplug.PluginManager;
import org.noear.solon.hotplug.PluginPackage;

import java.io.File;

/**
 * @author noear 2022/5/14 created
 */
public class Test5App {
    public static void main(String[] args) {
        Solon.start(Test5App.class, args);

        File jarFile = new File("/xxx/xxx.jar");

        //加载插件包
        PluginPackage jarPlugin = PluginManager.loadJar(jarFile).start();

        PluginManager.unloadJar(jarPlugin);
    }
}
