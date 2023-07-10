package org.noear.solon.admin.client.config;

import org.noear.solon.admin.client.controller.MonitorController;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        context.beanMake(MonitorController.class);
    }
}
