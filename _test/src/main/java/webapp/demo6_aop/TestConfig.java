package webapp.demo6_aop;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;

@XConfiguration
public class TestConfig {

    @XBean("rs3")
    public Rockapi build3(@XInject("rs1") Rockapi rs1){
        return new Rockservice3();
    }

    @XBean(value = "TestModel", typed = true, tags = "test")
    public TestModel build4(){
        return new TestModel("12");
    }
}
