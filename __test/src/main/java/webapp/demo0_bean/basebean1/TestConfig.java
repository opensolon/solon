package webapp.demo0_bean.basebean1;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2024/8/27 created
 */
@Configuration
public class TestConfig {
    @Bean(name = "a", typed = true)
    public A a() { //只注册实例和返回类型的，一级上层接口
        return new A() {
            @Override
            public void hello() {
                System.out.println("basebean1:A: hello");
            }
        };
    }

    @Inject
    B b; // C b 时，会出错

    @Init
    public void init(){
        b.hello();
    }
}
