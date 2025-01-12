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

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Metrics;
import org.noear.solon.Utils;
import org.noear.solon.cloud.metrics.annotation.MeterLongTimer;
import org.noear.solon.core.aspect.Invocation;

/**
 * 长计时器拦截器
 *
 * @author bai
 * @date 2.5
 */
public class MeterLongTimerInterceptor  extends BaseMeterInterceptor<MeterLongTimer, LongTaskTimer> {

    @Override
    protected MeterLongTimer getAnno(Invocation inv) {
        MeterLongTimer anno = inv.getMethodAnnotation(MeterLongTimer.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(MeterLongTimer.class);
        }
        return anno;
    }

    @Override
    protected String getAnnoName(MeterLongTimer anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterLongTimer anno) throws Throwable {
        String meterName = getMeterName(inv, anno);
        LongTaskTimer meter = getMeter(meterName, () -> {
            return LongTaskTimer.builder(getMeterName(inv, anno))
                    .tags(getMeterTags(inv, anno.tags()))
                    .publishPercentiles(anno.percentiles())
                    .description(anno.description())
                    .register(Metrics.globalRegistry);
        });

        //计时
        LongTaskTimer.Sample sample = meter.start(); //默认是 NANOSECONDS 计时
        try {
            return inv.invoke(); //改成 record, 不用再一层包异常
        } finally {
            sample.stop();
        }
    }
}
