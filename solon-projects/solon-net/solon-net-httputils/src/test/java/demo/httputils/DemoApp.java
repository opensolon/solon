package demo.httputils;

import org.noear.solon.Solon;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;

/**
 *
 * @author noear 2025/9/29 created
 *
 */
public class DemoApp {
    public static void main(String [] args) {
        HttpConfiguration.setFactory(JdkHttpUtilsFactory.getInstance());

        //在程序启动前，切换 httputils 的实现层
        Solon.start(DemoApp.class, args);
    }
}
