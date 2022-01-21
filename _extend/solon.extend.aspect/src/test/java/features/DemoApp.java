package features;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;
import org.noear.solon.extend.aspect.AspectUtil;
import thirdparty.Demo;

/**
 * @author noear 2022/1/21 created
 */
@Component
public class DemoApp {
    @Inject
    public Demo demo;

    //不需要扫描，直接系上
    //public Demo demo = AspectUtil.attach(new Demo(), DemoHandler.global);

    public static void main(String[] args){
        Solon.start(DemoApp.class, args, app->{
            AspectUtil.attachByScan("thirdparty", new DemoHandler());
        });

        Aop.get(DemoApp.class).demo.test();
    }
}
