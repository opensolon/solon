package org.noear.solon.extend.async.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.async.annotation.Async;
import org.noear.solon.extend.async.annotation.EnableAsync;

/**
 * @author noear
 * @since 1.6
 */
@Deprecated
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableAsync.class) == null) {
            return;
        }

        context.beanAroundAdd(Async.class, new AsyncInterceptor());
    }
}
