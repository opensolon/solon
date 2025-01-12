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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.noear.solon.Utils;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.core.aspect.Invocation;


/**
 * MeterCounter 拦截处理
 *
 * @author noear
 * @since 2.4
 */
public class MeterCounterInterceptor extends BaseMeterInterceptor<MeterCounter,Counter> {

    @Override
    protected MeterCounter getAnno(Invocation inv) {
        MeterCounter anno = inv.getMethodAnnotation(MeterCounter.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(MeterCounter.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterCounter anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterCounter anno) throws Throwable {
        String meterName = getMeterName(inv, anno);
        Counter meter = getMeter(meterName,()->{
            return Counter.builder(meterName)
                    .baseUnit(anno.unit())
                    .description(anno.description())
                    .tags(getMeterTags(inv, anno.tags()))
                    .register(Metrics.globalRegistry);
        });

        try {
            return inv.invoke();
        } finally {
            //计数
            meter.increment();
        }
    }
}
