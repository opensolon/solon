package com.fujieid.jap.solon.integration;

import com.fujieid.jap.solon.http.controller.SimpleController;
import com.fujieid.jap.solon.JapProps;
import com.fujieid.jap.solon.JapSolonConfig;
import com.fujieid.jap.solon.http.controller.SocialController;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(SolonApp app) {
        app.beanMake(JapProps.class);
        app.beanMake(JapSolonConfig.class);

        JapProps properties = Aop.get(JapProps.class);

        app.add(properties.getBasePath(), SimpleController.class);
        app.add(properties.getBasePath(), SocialController.class);
    }

}
