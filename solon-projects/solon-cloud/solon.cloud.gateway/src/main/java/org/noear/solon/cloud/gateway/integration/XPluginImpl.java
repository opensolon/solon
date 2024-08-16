package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.Solon;
import org.noear.solon.cloud.gateway.CloudGateway;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.9
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CloudGateway cloudGateway = CloudGatewayBuilder.build(context, "solon.cloud.gateway");

        if (cloudGateway != null) {
            Solon.app().http("/**", cloudGateway);
        }
    }
}
