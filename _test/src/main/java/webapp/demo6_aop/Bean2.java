package webapp.demo6_aop;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;


@Component
public class Bean2 {
    @Inject
    Bean1 bean1;

    public String name() {
        return "bean2";
    }

    public String namePlus() {
        return name() + bean1.name();
    }
}
