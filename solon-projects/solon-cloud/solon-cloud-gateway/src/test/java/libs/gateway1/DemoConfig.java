package libs.gateway1;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.gateway.CloudRouteRegister;

/**
 * @author noear 2024/8/18 created
 */
@Configuration
public class DemoConfig {
    @Bean
    public void init(CloudRouteRegister register){
        System.out.println("register........");
    }
}
