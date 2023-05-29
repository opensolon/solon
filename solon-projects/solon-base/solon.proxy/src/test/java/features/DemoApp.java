package features;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.proxy.ProxyUtil;
import thirdparty.Demo;

/**
 * @author noear 2022/1/21 created
 */
@Component
public class DemoApp {
    @Inject
    public Demo demo;


    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            //为一个类绑定拦截代理
            //AspectUtil.attach(Demo.class, DemoHandler.global);
            //为一批类绑定拦截代理
            ProxyUtil.attachByScan(app.context(), "thirdparty", DemoHandler.global);
        });

        Demo demo1 = Solon.context().getBean(DemoApp.class).demo;
        demo1.test();
    }
}
