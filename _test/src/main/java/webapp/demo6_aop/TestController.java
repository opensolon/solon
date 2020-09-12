package webapp.demo6_aop;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XInject;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@XController
public class TestController {
    @XInject("rs1") //会从bean库注入
    public Rockapi  rockapi11;

    @XInject("rs1")
    public Rockapi  rockapi12;

    @XInject("rs3")
    public Rockapi  rockapi13;

    @XInject //会自动生成
    public Rockservice2  rockapi2;


    @XInject
    public Rockapi  rockapi132;

    public String   test_aaa = XApp.cfg().get("demo6.test.aaa");
    public int      test_bbb = XApp.cfg().getInt("demo6.test.bbb",0);
    public Properties prop   = XApp.cfg().getProp("mytbae.bcf");

    @XMapping("/demo6/aop")
    public Object test() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("rockapi11", rockapi11.test());
        map.put("rockapi12", rockapi12.test());

        map.put("rockapi2", rockapi2.test());

        map.put("rockapi132", rockapi132.test());

        TestModel tmp = Aop.get(TestModel.class);
        if("12".equals(tmp.name) == false){
            return "出错了";
        }

        if("test".equals(Aop.factory().getWrap(TestModel.class).tags()) == false){
            return "出错了";
        }

        return map;
    }

    @XMapping("/demo6/aop3")
    public Object test3() throws Exception {
        return rockapi13.test();
    }
}
