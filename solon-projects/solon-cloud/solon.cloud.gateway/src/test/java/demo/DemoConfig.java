package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.gateway.CloudGatewayConfiguration;
import org.noear.solon.cloud.gateway.CloudRoute;

@Configuration
public class DemoConfig {
    @Bean
    public void init(CloudGatewayConfiguration configuration) {
        configuration
                .route("user-service", r -> r.uri("lb://user-service").path("/user/**"))
                .route(new CloudRoute().id("order-service").uri("lb://order-service").path("/order/**"));
    }
}
