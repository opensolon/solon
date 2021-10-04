package org.noear.solon.cloud.extend.health;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.get("/healthz", new HealthHandler());
    }
}
