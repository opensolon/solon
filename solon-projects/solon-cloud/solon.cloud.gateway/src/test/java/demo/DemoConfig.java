package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.gateway.CloudGatewayConfiguration;

@Configuration
public class DemoConfig {
    @Bean
    public void init(CloudGatewayConfiguration configuration) {
        configuration
                .route("user-service", r -> r.uri("lb://user-service").path("/user/**"))
                .route("order-service", r -> r.uri("lb://order-service").path("/order/**"));
    }
}
