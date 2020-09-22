package webapp.demo6_aop;

import org.noear.solon.annotation.XInject;

public class TestControllerBase {
    @XInject("rs1") //会从bean库注入
    public Rockapi  rockapi11;

    @XInject("rs1")
    public Rockapi  rockapi12;
}
