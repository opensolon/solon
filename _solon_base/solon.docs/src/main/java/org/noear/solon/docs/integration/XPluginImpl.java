package org.noear.solon.docs.integration;

import org.noear.solon.docs.openapi2.Swagger2Controller;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImpl implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.app().enableDoc() == false) {
            return;
        }

        Solon.app().add("/", Swagger2Controller.class);
    }
}
