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
        app.get("/healthz", ctx -> {
            HealthStatus healthStatus = HealthChecker.check();

            switch (healthStatus.getCode()) {
                case DOWN:
                    ctx.status(503);
                    break;
                case ERROR:
                    ctx.status(500);
                    break;
                default:
                    ctx.status(200);
            }

            ctx.outputAsJson(ONode.stringify(healthStatus));
        });
    }
}
