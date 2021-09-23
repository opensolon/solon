package webapp.demo6_aop;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
public class TestController extends TestControllerBase{

    @Inject("rs3")
    public Rockapi  rockapi13;

    @Inject //会自动生成
    public Rockservice2  rockapi2;


    @Inject
    public Rockapi  rockapi132;

    public String   test_aaa = Solon.cfg().get("demo6.test.aaa");
    public int      test_bbb = Solon.cfg().getInt("demo6.test.bbb",0);
    public Properties prop   = Solon.cfg().getProp("mytbae.bcf");

    @Mapping("/demo6/aop")
    public Object test() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("rockapi11", rockapi11.test());
        map.put("rockapi12", rockapi12.test());

        map.put("rockapi2", rockapi2.test());

        map.put("rockapi132", rockapi132.test());

        TestModel tmp = Aop.getOrNew(TestModel.class);
        if("12".equals(tmp.name) == false){
            return "出错了";
        }

        if("test".equals(Aop.context().getWrap(TestModel.class).tag()) == false){
            return "出错了";
        }

        return map;
    }

    @Mapping("/demo6/aop3")
    public Object test3() throws Exception {
        return rockapi13.test();
    }
}
