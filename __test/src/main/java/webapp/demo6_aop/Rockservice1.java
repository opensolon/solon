package webapp.demo6_aop;

import org.noear.solon.proxy.annotation.ProxyComponent;

@ProxyComponent("rs1")
public class Rockservice1 implements Rockapi {
    @Override
    public String test() {
        return "我是：Rockservice1";
    }
}
