package org.noear.solon.cloud.extend.opentracing.integration;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.opentracing.OpentracingProps;
import org.noear.solon.cloud.extend.opentracing.adapter.NamiFilterAdapter;
import org.noear.solon.cloud.extend.opentracing.adapter.SolonErrorAdapter;
import org.noear.solon.cloud.extend.opentracing.annotation.EnableOpentracing;
import org.noear.solon.cloud.extend.opentracing.adapter.SolonFilterAdapter;
import org.noear.solon.cloud.extend.opentracing.annotation.EnableTracing;
import org.noear.solon.cloud.extend.opentracing.annotation.Tracing;
import org.noear.solon.cloud.extend.opentracing.around.TracingInterceptor;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableOpentracing.class) == null &&
                app.source().getAnnotation(EnableTracing.class) == null) {
            return;
        }

        if (OpentracingProps.instance.getTraceEnable() == false) {
            return;
        }

        //添加 nami 适配
        NamiManager.reg(new NamiFilterAdapter());

        //添加 solon 适配
        app.filter(new SolonFilterAdapter());
        app.onError(new SolonErrorAdapter());

        //添加 @Tracing 适配
        Aop.context().beanAroundAdd(Tracing.class, new TracingInterceptor());
    }
}
