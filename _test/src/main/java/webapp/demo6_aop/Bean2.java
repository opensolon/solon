package webapp.demo6_aop;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XInject;

@XBean
public class Bean2 {
    @XInject
    Bean1 bean1;

    public String name() {
        return "bean2";
    }

    public String namePlus() {
        return name() + bean1.name();
    }
}
