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

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import org.noear.solon.Utils;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;
import org.noear.solon.core.aspect.Invocation;

import java.util.concurrent.TimeUnit;


/**
 * MeterTimer 拦截处理
 *
 * @author bai
 * @since 2.4
 */
public class MeterTimerInterceptor extends BaseMeterInterceptor<MeterTimer, Timer> {

    @Override
    protected MeterTimer getAnno(Invocation inv) {
        MeterTimer anno = inv.getMethodAnnotation(MeterTimer.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(MeterTimer.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterTimer anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterTimer anno) throws Throwable {
        //获取度量器
        String meterName = getMeterName(inv, anno);
        Timer meter = getMeter(meterName, () -> {
            return Timer.builder(meterName)
                    .description(anno.description())
                    .tags(getMeterTags(inv, anno.tags()))
                    .publishPercentiles(anno.percentiles())
                    .register(Metrics.globalRegistry);
        });

        //计时
        long start = System.currentTimeMillis();
        try {
            return inv.invoke();
        } finally {
            long span = System.currentTimeMillis() - start;
            meter.record(span, TimeUnit.MILLISECONDS);
        }
    }
}
