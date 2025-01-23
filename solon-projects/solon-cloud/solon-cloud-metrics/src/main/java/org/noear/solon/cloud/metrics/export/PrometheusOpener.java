/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
                appContext.beanPublish(beanWrap);
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
