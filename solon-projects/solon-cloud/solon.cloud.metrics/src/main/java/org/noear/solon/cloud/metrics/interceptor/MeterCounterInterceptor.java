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
        MeterCounter anno = inv.method().getAnnotation(MeterCounter.class);
        if (anno == null) {
            anno = inv.target().getClass().getAnnotation(MeterCounter.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterCounter anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterCounter anno) throws Throwable {
        //获取度量器
        Counter meter = meterCached.get(anno);
        if (meter == null) {
            synchronized (anno) {
                meter = meterCached.get(anno);
                if (meter == null) {
                    String meterName = getMeterName(inv, anno);

                    meter = Counter.builder(meterName)
                            .tags(getMeterTags(inv, anno.tags()))
                            .description(anno.description())
                            .register(Metrics.globalRegistry);

                    meterCached.put(anno, meter);
                }
            }
        }

        try {
            return inv.invoke();
        } finally {
            //计数
            meter.increment();
        }
    }
}
