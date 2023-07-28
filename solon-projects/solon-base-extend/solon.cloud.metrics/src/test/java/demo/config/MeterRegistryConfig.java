package demo.config;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * 我普罗米修斯计注册表
 *
 * @author bai
 * @date 2023/07/26
 */
@Configuration
public class MeterRegistryConfig {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        PrometheusMeterRegistry tmp = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        //tmp.scrape(TextFormat.CONTENT_TYPE_OPENMETRICS_100);
        return tmp;
    }
}
