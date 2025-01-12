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

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Metrics;
import org.noear.solon.Utils;
import org.noear.solon.cloud.metrics.annotation.MeterSummary;
import org.noear.solon.core.aspect.Invocation;


/**
 * MeterSummary 拦截处理
 *
 * @author bai
 * @since 2.4
 */
public class MeterSummaryInterceptor extends BaseMeterInterceptor<MeterSummary, DistributionSummary> {

    @Override
    protected MeterSummary getAnno(Invocation inv) {
        MeterSummary anno = inv.getMethodAnnotation(MeterSummary.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(MeterSummary.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterSummary anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterSummary anno) throws Throwable {
        String meterName = getMeterName(inv, anno);
        DistributionSummary meter = getMeter(meterName, ()->{
            DistributionSummary.Builder builder = DistributionSummary
                    .builder(meterName)
                    .tags(getMeterTags(inv, anno.tags()));

            //最大期望值
            if (anno.maxValue() != Double.MAX_VALUE) {
                builder.maximumExpectedValue(anno.maxValue());
            }

            //最小期望值
            if (anno.minValue() != Double.MIN_VALUE) {
                builder.minimumExpectedValue(anno.minValue());
            }

            builder.scale(anno.scale());
            builder.publishPercentileHistogram(anno.percentilesHistogram());
            builder.publishPercentiles(anno.percentiles());
            builder.serviceLevelObjectives(anno.serviceLevelObjectives());

            return builder.register(Metrics.globalRegistry);
        });

        Object rst = inv.invoke();

        //计变数
        if (rst instanceof Number) {
            meter.record(((Number) rst).doubleValue());
        }
        return rst;
    }
}