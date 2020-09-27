package webapp.demo6_aop;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XInject;

@XBean
public class Bean1 {
    @XInject
    Bean2 bean2;

    public String name(){
        return "bean1";
    }
}
