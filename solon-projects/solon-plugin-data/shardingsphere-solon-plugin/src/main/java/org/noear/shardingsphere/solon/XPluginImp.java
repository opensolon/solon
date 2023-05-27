package org.noear.shardingsphere.solon;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author sorghum
 * @since 2.3.1
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanScan("org.noear.shardingsphere.solon");
    }

    @java.lang.Override
    public void prestop() throws Throwable {
        Plugin.super.prestop();
    }

    @java.lang.Override
    public void stop() throws Throwable {
        Plugin.super.stop();
    }
}
