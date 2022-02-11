package webapp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
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

        //加载jar
        File jarFile = new File("/xxx/xxx.jar");
        ExtendLoader.loadJar(jarFile);

        //直接插入
        app.plug(new PluginImpl());

        //拨出
        PluginEntity tmp = app.pluginPop(PluginImpl.class);
        if(tmp != null) {
            //停掉插件
            tmp.prestop();
            tmp.stop();
        }
    }

    public static class PluginImpl implements Plugin{
        @Override
        public void start(SolonApp app) {
            System.out.println("on start");
        }

        @Override
        public void stop() throws Throwable {
            System.out.println("on stop");
        }
    }
}
