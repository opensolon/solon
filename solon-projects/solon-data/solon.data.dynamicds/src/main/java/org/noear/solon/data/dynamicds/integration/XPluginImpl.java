package org.noear.solon.data.dynamicds.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.dynamicds.DynamicDs;
import org.noear.solon.data.dynamicds.DynamicDsInterceptor;

/**
 * @author noear
 * @since 2.5
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        context.beanInterceptorAdd(DynamicDs.class, new DynamicDsInterceptor());
    }
}
