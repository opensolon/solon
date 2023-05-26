package org.noear.solon.scheduling.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.Async;
import org.noear.solon.scheduling.annotation.EnableAsync;
import org.noear.solon.scheduling.async.AsyncInterceptor;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        Annotation enableAnno = Solon.app().source().getAnnotation(EnableAsync.class);

        if (enableAnno != null) {
            context.beanAroundAdd(Async.class, new AsyncInterceptor(context), Integer.MIN_VALUE);
        }
    }
}
