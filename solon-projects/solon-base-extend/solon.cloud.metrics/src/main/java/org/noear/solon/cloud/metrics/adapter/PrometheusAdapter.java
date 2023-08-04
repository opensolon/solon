package org.noear.solon.cloud.metrics.adapter;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.ClassUtil;

/**
 * Prometheus 自动适配器
 *
 * @author noear
 * @since 2.4
 */
public class PrometheusAdapter implements MetricsAdapter {
    @Override
    public void adaptive(AopContext aopContext) {
        final String PATH = "/metrics/prometheus";

        //如果有这个类：PrometheusMeterRegistry
        if (ClassUtil.hasClass(() -> PrometheusMeterRegistry.class)) {
            final PrometheusMeterRegistry registry;

            if (aopContext.hasWrap(PrometheusMeterRegistry.class)) {
                registry = aopContext.getBean(PrometheusMeterRegistry.class);
            } else {
                registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
                BeanWrap beanWrap = aopContext.wrapAndPut(PrometheusMeterRegistry.class, registry);
                aopContext.wrapPublish(beanWrap);
            }

            Solon.app().get(PATH, ctx -> {
                ctx.output(registry.scrape());
            });
        }
    }
}
