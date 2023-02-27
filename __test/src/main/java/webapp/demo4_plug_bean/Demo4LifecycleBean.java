package webapp.demo4_plug_bean;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.bean.LifecycleBean;

@Component
public class Demo4LifecycleBean implements LifecycleBean {
    @Override
    public void afterPropertiesSet() throws Throwable {
        Solon.app().get("/demo4/*", c -> c.output("是插件生出了我..."));
    }
}
