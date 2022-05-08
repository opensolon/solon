package org.noear.solon.cloud.extend.jaeger;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.jaeger.service.JaegerTracerFactory;
import org.noear.solon.cloud.tracing.TracingManager;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.7
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (JaegerProps.instance.getTraceEnable() == false) {
            return;
        }

        TracingManager.enable(JaegerProps.instance.getTraceExclude());
        TracingManager.register(new JaegerTracerFactory(JaegerProps.instance));
    }
}
