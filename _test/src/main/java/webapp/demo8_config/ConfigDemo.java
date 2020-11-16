package webapp.demo8_config;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.Properties;

@Configuration
public class ConfigDemo {
    //注解方式(仅支持简单的类型注入)
    @Inject("${username}")
    public String username;

    @Inject("${paasword}")
    public int paasword;

    @Inject("${demo8.test}")
    public Properties test;

    //获取方式（复杂的请用这种方式）
    //
    public String nameuser_2 = Solon.cfg().get("username");

    public Properties dbcfg = Solon.cfg().getProp("demo8.test");

    public Properties test2(){
        return Solon.cfg().getProp("demo8.test");
    }

    public ConfigDemo(){
        Solon.cfg().onChange((k, v)->{
            if("username".equals(k)){
                username = v;
            }
        });
    }
}
