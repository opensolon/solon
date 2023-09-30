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
        MeterTimer anno = inv.method().getAnnotation(MeterTimer.class);
        if (anno == null) {
            anno = inv.target().getClass().getAnnotation(MeterTimer.class);
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
