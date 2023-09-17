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
        MeterSummary anno = inv.method().getAnnotation(MeterSummary.class);
        if (anno == null) {
            anno = inv.target().getClass().getAnnotation(MeterSummary.class);
        }

        return anno;
    }

    @Override
    protected String getAnnoName(MeterSummary anno) {
        return Utils.annoAlias(anno.value(), anno.name());
    }

    @Override
    protected Object metering(Invocation inv, MeterSummary anno) throws Throwable {

        DistributionSummary meter;
        String meterName = getMeterName(inv, anno);
        if (!meterCached.containsKey(meterName)) {

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

            if(anno.percentiles().length > 0) {
                builder.publishPercentiles(anno.percentiles());
            }

            meter = builder.register(Metrics.globalRegistry);
            meterCached.put(meterName, meter);
        }
        //获取度量器
        meter = meterCached.get(meterName);
        Object rst = inv.invoke();

        //计变数
        if (rst instanceof Number) {
            meter.record(((Number) rst).doubleValue());
        }
        return rst;
    }
}