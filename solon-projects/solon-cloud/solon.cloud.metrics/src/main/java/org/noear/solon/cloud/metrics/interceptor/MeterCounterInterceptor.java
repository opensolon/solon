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
