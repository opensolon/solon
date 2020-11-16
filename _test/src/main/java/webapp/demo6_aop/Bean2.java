package webapp.demo6_aop;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Inject;

@Bean
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
