package org.noear.solon.cloud.extend.health;

import org.noear.snack.ONode;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.get("/healthz", context -> {
            HealthStatus healthStatus = HealthChecker.getHealthChecker()
                    .checkAll();
            switch (healthStatus.getOutcome()) {
                case DOWN: context.status(503); break;
                case ERROR: context.status(500); break;
                default: context.status(200);
            }
            context.output(ONode.stringify(healthStatus));
        });

    }
}
