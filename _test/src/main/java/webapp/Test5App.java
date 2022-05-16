package webapp;

import org.noear.solon.Solon;
import org.noear.solon.plugind.PluginManager;

import java.io.File;

/**
 * @author noear 2022/5/14 created
 */
public class Test5App {
    public static void main(String[] args) {
        Solon.start(Test5App.class, args);

        File jarFile = new File("/xxx/xxx.jar");

        //加载插件包
        PluginManager.loadJar(jarFile).start();
    }
}
