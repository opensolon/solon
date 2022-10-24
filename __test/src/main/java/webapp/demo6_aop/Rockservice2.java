package webapp.demo6_aop;

import org.noear.solon.aspect.annotation.Service;

@Service("rs2")
public class Rockservice2 implements Rockapi {

    @Override
    public String test() {
        return "我是：Rockservice2";
    }
}
