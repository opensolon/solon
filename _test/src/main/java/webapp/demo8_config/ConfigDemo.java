package webapp.demo8_config;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XInject;
import org.noear.solon.core.Aop;

import java.util.Properties;

@XBean("config")
public class ConfigDemo {
    //注解方式(仅支持简单的类型注入)
    @XInject("username")
    public String username;

    @XInject("paasword")
    public int paasword;

    @XInject("demo8.test")
    public Properties test;

    //获取方式（复杂的请用这种方式）
    //
    public String nameuser_2 = Aop.prop().get("username");

    public Properties dbcfg = Aop.prop().getProp("demo8.test");

    public Properties test2(){
        return Aop.prop().getProp("demo8.test");
    }

    public ConfigDemo(){
        Aop.prop().onChange((k,v)->{
            if("username".equals(k)){
                username = v;
            }
        });
    }
}
