package org.noear.solon.guard.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.guard.annotation.GuardInject;

/**
 * @author noear
 * @since 1.9
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanInjectorAdd(GuardInject.class, new GuardBeanInjector());
    }
}
