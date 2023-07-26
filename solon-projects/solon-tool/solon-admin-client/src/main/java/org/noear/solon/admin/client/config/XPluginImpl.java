package org.noear.solon.admin.client.config;

import org.noear.solon.Solon;
import org.noear.solon.admin.client.annotation.EnableAdminClient;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        if(Solon.app().source().isAnnotationPresent(EnableAdminClient.class) == false){
            return;
        }

        context.beanScan("org.noear.solon.admin.client");
    }
}
