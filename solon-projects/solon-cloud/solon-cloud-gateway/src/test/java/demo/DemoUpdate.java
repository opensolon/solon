package demo;

import org.noear.solon.annotation.Inject;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.gateway.CloudRouteRegister;
import org.noear.solon.cloud.gateway.properties.GatewayProperties;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.core.Props;

import java.util.Properties;

/**
 * @author noear 2025/3/20 created
 */
@CloudConfig("demo.yml")
public class DemoUpdate implements CloudConfigHandler {
    @Inject
    CloudRouteRegister routeRegister;

    @Override
    public void handle(Config config) {
        final GatewayProperties gatewayProperties = new Props(config.toProps())
                .getProp(GatewayProperties.SOLON_CLOUD_GATEWAY)
                .toBean(GatewayProperties.class);

        routeRegister.route(gatewayProperties);
    }
}