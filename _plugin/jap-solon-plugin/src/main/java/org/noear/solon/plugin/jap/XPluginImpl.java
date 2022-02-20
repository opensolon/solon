package org.noear.solon.plugin.jap;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.plugin.jap.controller.SimpleController;
import org.noear.solon.plugin.jap.properties.JapProperties;
import org.noear.solon.plugin.jap.properties.JapSolonConfig;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(SolonApp app) {
        app.beanMake(JapProperties.class);
        app.beanMake(JapSolonConfig.class);

        JapSolonConfig japSolonConfig = Aop.get(JapSolonConfig.class);
        if (null != japSolonConfig) {
            japSolonConfig.initStrategy();
        }

        JapProperties properties = Aop.get(JapProperties.class);

        app.add(properties.getBasePath(), SimpleController.class);
    }
}
