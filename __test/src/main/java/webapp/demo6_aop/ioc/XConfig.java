package webapp.demo6_aop.ioc;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/9/24 created
 */
@Configuration
public class XConfig {
    @Bean
    @Condition(onMissingBean = Service1.class)
    public Service1 service1(Api1 api1, @Inject(required = false) Api0 api0) {
        return new Service1Impl(api1, api0);
    }

    @Bean
    @Condition(onMissingBean = Service2.class)
    public Service2 service2(Api1 api1, Api2 api2, Service1 service1) {
        return new Service2Impl(api1, api2, service1);
    }

    @Bean
    @Condition(onMissingBean = Service3.class)
    public Service3 service3(Api3 api3, @Inject(required = false) Api0 api0, Service1 service1, Service2 service2) {
        return new Service3Impl(api3, api0, service1, service2);
    }

    @Bean
    public TestCom1 com1(Service1 service1, Service2 service2, Service3 service3) {
        return new TestCom1(service1, service2, service3);
    }

    @Bean
    public TestCom2 com2(TestCom1 com1) {
        return new TestCom2();
    }
}
