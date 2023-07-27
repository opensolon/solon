package test;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.noear.solon.micrometer.AbsMeterRegistry;
import org.noear.solon.annotation.Configuration;

/**
 * 我普罗米修斯计注册表
 *
 * @author bai
 * @date 2023/07/26
 */
@Configuration
public class MyPrometheusMeterRegistry extends AbsMeterRegistry<PrometheusMeterRegistry> {


    public MyPrometheusMeterRegistry() {
        super(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
    }

    /**
     *  注册器消息体内容
     *
     * @param prometheusMeterRegistry 普罗米修斯计注册表
     * @return {@link String}x
     */
    @Override
    public String scrape(PrometheusMeterRegistry prometheusMeterRegistry) {
        return prometheusMeterRegistry.scrape(TextFormat.CONTENT_TYPE_OPENMETRICS_100);
    }

    /**
     * 注册表
     *
     * @param meterRegistry 计注册表
     */
    @Override
    public void registry(MeterRegistry meterRegistry){
        // 全局注册
        super.registry(meterRegistry);
    }
}
