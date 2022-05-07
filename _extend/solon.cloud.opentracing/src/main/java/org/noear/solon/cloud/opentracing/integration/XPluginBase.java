package org.noear.solon.cloud.opentracing.integration;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.cloud.opentracing.annotation.Tracing;
import org.noear.solon.cloud.opentracing.annotation.TracingInterceptor;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginBase implements Plugin {

    @Override
    public void start(SolonApp app) {
        //添加 nami 适配
        NamiManager.reg(new NamiFilterAdapter());

        //添加 solon 适配
        app.filter(new SolonFilterAdapter());
        app.onError(new SolonErrorAdapter());

        //添加 @Tracing 适配
        Aop.context().beanAroundAdd(Tracing.class, new TracingInterceptor());
    }
}
