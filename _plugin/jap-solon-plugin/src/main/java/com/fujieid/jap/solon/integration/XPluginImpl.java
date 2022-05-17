package com.fujieid.jap.solon.integration;

import com.fujieid.jap.solon.JapInitializer;
import com.fujieid.jap.solon.JapProps;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(AopContext context) {
        app.beanMake(JapProps.class);
        app.beanMake(JapInitializer.class);
    }

}
