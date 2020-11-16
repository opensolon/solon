package webapp.demo6_aop;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class TestConfig {

    @Bean("rs3")
    public Rockapi build3(@Inject("rs1") Rockapi rs1){
        return new Rockservice3();
    }

    @Bean(value = "TestModel", typed = true, tag = "test")
    public TestModel build4(){
        return new TestModel("12");
    }
}
