package io.swagger.solon.integration;


import io.swagger.solon.api.SwaggerController;
import io.swagger.solon.annotation.EnableSwagger;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().isAnnotationPresent(EnableSwagger.class) == false) {
            return;
        }

        Solon.app().add("/", SwaggerController.class);
    }
}
