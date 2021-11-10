package demo;

import org.noear.solon.cloud.impl.CloudLoadBalanceFactory;

/**
 * @author noear 2021/11/10 created
 */
public class LoadBalanceDemo {
    public void register(){
        CloudLoadBalanceFactory.instance
                .register("","userapi",()-> "http://userapi");
    }
}
