package org.noear.solon.plugin.jap;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.plugin.jap.controller.SimpleController;
import org.noear.solon.plugin.jap.properties.JapProperties;
import org.noear.solon.plugin.jap.properties.JapSoloConfig;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.beanMake(JapProperties.class);
        app.beanMake(JapSoloConfig.class);
        app.beanMake(SimpleController.class);

        JapSoloConfig japSoloConfig = Aop.get(JapSoloConfig.class);
        if (null != japSoloConfig) {
            japSoloConfig.initStrategy();
        }
    }
}
