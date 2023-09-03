package org.noear.solon.cloud.metrics.export;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ClassUtil;

/**
 * Prometheus 度量开放器（如果有引入包，则自动生效）
 *
 * @author noear
 * @since 2.4
 */
public class PrometheusOpener implements MeterOpener {
    PrometheusMeterRegistry registry;

    @Override
    public String path() {
        return "/metrics/prometheus";
    }

    @Override
    public boolean isSupported(AppContext appContext) {
        if (registry != null) {
            return true;
        }

        if (ClassUtil.hasClass(() -> PrometheusMeterRegistry.class)) {
            //如果有这个类
            if (appContext.hasWrap(PrometheusMeterRegistry.class)) {
                registry = appContext.getBean(PrometheusMeterRegistry.class);
            } else {
                registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
                BeanWrap beanWrap = appContext.wrapAndPut(PrometheusMeterRegistry.class, registry);
                appContext.wrapPublish(beanWrap);
            }

            return true;
        }

        return false;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        //TextFormat.CONTENT_TYPE_OPENMETRICS_100
        //TextFormat.CONTENT_TYPE_004
        ctx.output(registry.scrape());
    }
}
