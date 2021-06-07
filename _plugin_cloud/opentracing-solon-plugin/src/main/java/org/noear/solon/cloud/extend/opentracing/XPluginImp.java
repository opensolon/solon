package org.noear.solon.cloud.extend.opentracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.opentracing.adapter.NamiInterceptorAdapter;
import org.noear.solon.cloud.extend.opentracing.annotation.EnableOpentracing;
import org.noear.solon.cloud.extend.opentracing.adapter.SolonFilterAdapter;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    private Tracer tracer;

    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableOpentracing.class) == null) {
            return;
        }

        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });

        app.filter(new SolonFilterAdapter());
        NamiManager.reg(new NamiInterceptorAdapter());

        app.onError(e -> {
            if (tracer == null) {
                return;
            }

            Span span = tracer.activeSpan();

            if (span != null) {
                span.log(Utils.throwableToString(e));
            }
        });
    }
}
