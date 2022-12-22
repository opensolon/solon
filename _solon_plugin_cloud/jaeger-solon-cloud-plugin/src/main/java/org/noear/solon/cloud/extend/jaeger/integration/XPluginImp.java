package org.noear.solon.cloud.extend.jaeger.integration;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.jaeger.service.JaegerTracerFactory;
import org.noear.solon.cloud.tracing.TracingManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.7
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context, "jaeger");

        if (cloudProps.getTraceEnable() == false) {
            return;
        }

        if (Utils.isEmpty(cloudProps.getServer())) {
            return;
        }

        TracingManager.enable(cloudProps.getTraceExclude());
        TracingManager.register(new JaegerTracerFactory(cloudProps));
    }
}