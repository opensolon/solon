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
