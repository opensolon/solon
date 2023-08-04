package org.noear.solon.cloud.metrics.opener;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ClassUtil;

/**
 * Prometheus 自动开放器（如果有引入包，则自动生效）
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
    public boolean isRegistered(AopContext aopContext) {
        if (registry != null) {
            return true;
        }

        if (ClassUtil.hasClass(() -> PrometheusMeterRegistry.class)) {
            //如果有这个类
            if (aopContext.hasWrap(PrometheusMeterRegistry.class)) {
                registry = aopContext.getBean(PrometheusMeterRegistry.class);
            } else {
                registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
                BeanWrap beanWrap = aopContext.wrapAndPut(PrometheusMeterRegistry.class, registry);
                aopContext.wrapPublish(beanWrap);
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
