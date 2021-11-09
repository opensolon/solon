package org.noear.solon.extend.health.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.health.HealthHandler;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //
        // HealthHandler 独立出来，便于其它检测路径的复用
        //
        app.get(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());
        app.head(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());
    }
}
