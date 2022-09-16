package webapp.demo6_aop;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;

@Component("rs1")
public class Rockservice1 implements Rockapi {
    @Override
    public String test() {
        return "我是：Rockservice1";
    }
}
