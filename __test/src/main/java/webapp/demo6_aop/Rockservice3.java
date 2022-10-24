package webapp.demo6_aop;


import org.noear.solon.aspect.annotation.Service;

@Service
public class Rockservice3 implements Rockapi {

    @Override
    public String test() {
        return "我是：Rockservice3";
    }
}
