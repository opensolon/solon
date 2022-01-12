package org.noear.solon.extend.async.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.async.AsyncInterceptor;
import org.noear.solon.extend.async.annotation.Async;
import org.noear.solon.extend.async.annotation.EnableAsync;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableAsync.class) == null) {
            return;
        }

        Aop.context().beanAroundAdd(Async.class, new AsyncInterceptor());
    }
}
