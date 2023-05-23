package org.noear.solon.cloud.extend.zipkin.integration;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.zipkin.service.ZipkinTracerFactory;
import org.noear.solon.cloud.tracing.TracingManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author blackbear2003
 * @since 2.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context, "zipkin");

        if (cloudProps.getTraceEnable() == false) {
            return;
        }

        if (Utils.isEmpty(cloudProps.getServer())) {
            return;
        }

        TracingManager.enable(cloudProps.getTraceExclude());
        TracingManager.register(new ZipkinTracerFactory(cloudProps));
    }
}
