package webapp;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.PluginPackage;

import java.io.File;

/**
 * @author noear 2022/5/14 created
 */
public class Test5App {
    public static void main(String[] args){
        Solon.start(Test5App.class, args);

        File jarFile = new File("/xxx/xx.jar");

        //加载插件包
        PluginPackage pluginPackage = ExtendLoader.loadPluginJar(jarFile);
        //启动插件包
        pluginPackage.start();


        //卸载Jar插件包
        ExtendLoader.unloadPluginJar(pluginPackage);
    }
}
