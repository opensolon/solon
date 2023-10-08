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
