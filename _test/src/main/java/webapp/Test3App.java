package webapp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.PluginEntity;

import java.io.File;

/**
 * @author noear 2022/2/11 created
 */
public class Test3App {
    public static void main(String[] args) {
        SolonApp app = Solon.start(Test3App.class, args, x->{
            //添加插件（随启动一起启动）
            //x.pluginAdd(0, new PluginImpl());
        });

        //直接插入
        app.plug(new PluginImpl()); //

        //拨出
        PluginEntity tmp = app.pluginPop(PluginImpl.class);
        if(tmp != null) {
            //停掉插件
            tmp.prestop();
            tmp.stop();
        }
    }

    //热加载demo
    void demo_hotadd(SolonApp app) {
        //热加载jar
        File jarFile = new File("/xxx/xxx.jar");
        ExtendLoader.loadJar(jarFile);

        //热实例化插件类class
        Plugin jarPlugin = Utils.newInstance("xxx.xxx.xxx.xx");

        //插入(内部会直接调用)
        if (jarPlugin != null) {
            app.plug(jarPlugin);
        }
    }

    //热拨出
    void demo_hotpop(SolonApp app){
        Class<?> pluginClz = Utils.loadClass("xxx.xxx.xxx.xx");

        PluginEntity tmp = app.pluginPop(pluginClz);
        if(tmp != null) {
            //停掉插件
            tmp.prestop();
            tmp.stop();
        }

        //移除jar包
        File jarFile = new File("/xxx/xxx.jar");
        ExtendLoader.unloadJar(jarFile);
    }

    public static class PluginImpl implements Plugin{
        @Override
        public void start(AopContext context) {
            System.out.println("on start");
        }

        @Override
        public void stop() throws Throwable {
            System.out.println("on stop");
        }
    }
}
