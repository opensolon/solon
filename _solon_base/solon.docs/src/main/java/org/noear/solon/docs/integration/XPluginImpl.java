package org.noear.solon.docs.integration;


import org.noear.solon.docs.swagger2.SwaggerController;
import org.noear.solon.docs.annotation.EnableSwagger2;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImpl implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().isAnnotationPresent(EnableSwagger2.class) == false) {
            return;
        }

        Solon.app().add("/", SwaggerController.class);
    }
}
