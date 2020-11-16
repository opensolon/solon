package webapp.demo6_aop;

import org.noear.solon.annotation.Inject;

public class TestControllerBase {
    @Inject("rs1") //会从bean库注入
    public Rockapi  rockapi11;

    @Inject("rs1")
    public Rockapi  rockapi12;
}
