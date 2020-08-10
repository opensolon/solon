package webapp.demo6_aop;


import org.noear.solon.annotation.XBean;

@XBean
public class Rockservice3 implements Rockapi {

    @Override
    public String test() {
        return "我是：Rockservice3";
    }
}
