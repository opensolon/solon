package webapp.demo6_aop;


import org.noear.solon.annotation.Component;

@Component
public class Rockservice3 implements Rockapi {

    @Override
    public String test() {
        return "我是：Rockservice3";
    }
}
