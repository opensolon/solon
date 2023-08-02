package org.noear.solon.cloud.metrics.Interceptor;

import io.micrometer.core.instrument.Metrics;
import org.noear.solon.Utils;
import org.noear.solon.cloud.metrics.annotation.MeterGauge;
import org.noear.solon.core.aspect.Invocation;


/**
 * MeterTimer 拦截处理
 *
 * @author bai
 * @since 2.4
 */
public class MeterGaugeInterceptor extends BaseMeterInterceptor<MeterGauge, Number> {

    @Override
    protected MeterGauge getAnno(Invocation inv) {
        MeterGauge anno = inv.method().getAnnotation(MeterGauge.class);
        if (anno == null) {
            anno = inv.target().getClass().getAnnotation(MeterGauge.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterGauge anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterGauge anno) throws Throwable {
        Object rst = inv.invoke();

        //计变数
        if (rst instanceof Number) {
            String meterName = getMeterName(inv, anno);
            Metrics.gauge(meterName, getMeterTags(inv, anno.tags()), (Number) rst);
        }

        return rst;
    }
}
