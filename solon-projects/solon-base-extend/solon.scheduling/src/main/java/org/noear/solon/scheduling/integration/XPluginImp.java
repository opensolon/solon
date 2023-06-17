package org.noear.solon.scheduling.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.scheduling.annotation.*;
import org.noear.solon.scheduling.async.AsyncInterceptor;
import org.noear.solon.scheduling.retry.RetryInterceptor;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        Class<?> source = Solon.app().source();

        // @since 2.2
        Annotation enableAnno = source.getAnnotation(EnableAsync.class);
        if (enableAnno != null) {
            context.beanAroundAdd(Async.class, new AsyncInterceptor(context));
        }

        // @since 2.3
        Annotation enableRetryAnno = source.getAnnotation(EnableRetry.class);
        if (enableRetryAnno != null) {
            context.beanAroundAdd(Retry.class, new RetryInterceptor(context), Integer.MIN_VALUE);
        }
    }
}
