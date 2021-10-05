package org.noear.solon.extend.health;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

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
        app.all(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());
    }
}
