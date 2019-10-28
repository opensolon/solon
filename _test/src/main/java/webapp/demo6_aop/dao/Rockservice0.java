package webapp.demo6_aop.dao;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XInject;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import webapp.demo6_aop.dao.Rockapi;

import java.util.Properties;

@XController
public class Rockservice0 {
    @XInject //支持bean注入
    public Rockapi  rockapi1;
    @XInject("rs2")
    public Rockapi  rockapi2;
    public String   test_aaa = Aop.prop().get("demo6.test.aaa");
    public int      test_bbb = Aop.prop().getInt("demo6.test.bbb",0);
    public Properties prop   = Aop.prop().getProp("mytbae.bcf");

    @XMapping("/demo6/test")
    public void test() throws Exception{
        if(rockapi1 != null){
            test_aaa =  rockapi1.test1(12);
        }

        if(rockapi2 != null){
            test_aaa =  rockapi2.test1(12);
        }

        XContext.current().output(test_aaa);
    }
}
