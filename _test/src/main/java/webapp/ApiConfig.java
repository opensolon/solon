package webapp;

import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2021/3/2 created
 */
@Configuration
public class ApiConfig {
    @Bean
    public Info buildInfo(){
        Info info = new Info();
        info.description("Solon framework test");
        info.contact(new Contact().name("noear").email("noear@zzz.org"));
        info.license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html"));
        info.title("框架测试接口");


        return info;
    }
}
