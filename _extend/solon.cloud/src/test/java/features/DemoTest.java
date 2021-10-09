package features;

import org.noear.solon.cloud.impl.CloudLoadBalanceFactory;

/**
 * @author noear 2021/10/9 created
 */
public class DemoTest {
    public void xxx(){
        CloudLoadBalanceFactory.instance
                .register("","userapi",()-> "http://userapi");
    }
}
