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
package org.noear.solon.cloud.metrics.interceptor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import org.noear.solon.Utils;
import org.noear.solon.cloud.metrics.annotation.MeterGauge;
import org.noear.solon.core.aspect.Invocation;

import java.util.concurrent.atomic.AtomicReference;


/**
 * MeterGauge 拦截处理
 *
 * @author bai
 * @since 2.4
 */
public class MeterGaugeInterceptor extends BaseMeterInterceptor<MeterGauge, AtomicReference<Double>> {

    @Override
    protected MeterGauge getAnno(Invocation inv) {
        MeterGauge anno = inv.getMethodAnnotation(MeterGauge.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(MeterGauge.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterGauge anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterGauge anno) throws Throwable {
        String meterName = getMeterName(inv, anno);
        AtomicReference<Double> meter = getMeter(meterName, () -> {
            AtomicReference<Double> numberReference = new AtomicReference<>();

            Gauge.builder(meterName, numberReference, AtomicReference<Double>::get)
                    .tags(getMeterTags(inv, anno.tags()))
                    .description(anno.description())
                    .register(Metrics.globalRegistry);

            return numberReference;
        });

        Object rst = inv.invoke();

        //计变数
        if (rst instanceof Number) {
            meter.set(((Number) rst).doubleValue());
        }

        return rst;
    }
}
