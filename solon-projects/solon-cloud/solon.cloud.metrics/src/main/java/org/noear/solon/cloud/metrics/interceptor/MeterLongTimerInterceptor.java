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
 * @date 2023/08/04
 */
public class MeterLongTimerInterceptor  extends BaseMeterInterceptor<MeterLongTimer, Number> {

    @Override
    protected MeterLongTimer getAnno(Invocation inv) {
        MeterLongTimer anno = inv.method().getAnnotation(MeterLongTimer.class);
        if (anno == null) {
            anno = inv.target().getClass().getAnnotation(MeterLongTimer.class);
        }
        return anno;
    }

    @Override
    protected String getAnnoName(MeterLongTimer anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterLongTimer anno) throws Throwable {
        LongTaskTimer longTaskTimer = LongTaskTimer.builder(getMeterName(inv, anno))
                .tags(getMeterTags(inv, anno.tags()))
                .publishPercentiles(anno.percentiles())
                .description(anno.description())
                .register(Metrics.globalRegistry);
        return longTaskTimer.record(()->{
            try {
                return inv.invoke();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
