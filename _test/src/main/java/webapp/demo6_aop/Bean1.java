package webapp.demo6_aop;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class Bean1 {
    @Inject
    Bean2 bean2;

    public String name(){
        return "bean1";
    }

    @ExTest
    public void test(){

    }
}
