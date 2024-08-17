package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.gateway.CloudGatewayConfiguration;

@Configuration
public class DemoConfig {
    @Bean
    public void init(CloudGatewayConfiguration configuration) {
        configuration
                .route("user-service", r -> r.path("/user/**").upstream("lb://user-service"))
                .route("order-service", r -> r.path("/order/**").upstream("lb://order-service"));
    }
}
