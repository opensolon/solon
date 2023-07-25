package org.noear.solon.admin.client.config;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.concurrent.TimeUnit;

/**
 * 配置文件检查和注入
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Slf4j
@Configuration
public class AdminClientBootstrapConfiguration {

    @Condition(onProperty = "${solon.admin.client.mode:local} = local")
    @Bean
    public IClientProperties localClientProperties(@Inject(value = "${solon.admin.client}") LocalClientProperties properties) {
        log.debug("Injected localClientProperties: " + properties);
        return properties;
    }


    @Condition(onProperty = "${solon.admin.client.mode:local} = cloud")
    @Bean
    public IClientProperties cloudClientProperties(@Inject(value = "${solon.admin.client}") CloudClientProperties properties) {
        log.debug("Injected cloudClientProperties: " + properties);
        return properties;
    }

    @Bean
    public MarkedClientEnabled markedClientEnabled(@Inject(required = false) IClientProperties clientProperties) {
        if (clientProperties == null || !clientProperties.isEnabled()) {
            log.error("Failed to enable Solon Admin client.", new IllegalStateException("Could not enable Solon Admin client because none of the properties has been configured correctly."));
            return null;
        }
        return new MarkedClientEnabled(clientProperties.getMode());
    }

    @Value
    public static class MarkedClientEnabled {
        String mode;

        public MarkedClientEnabled(String mode) {
            this.mode = mode;

            log.info("Solon Admin client has been successfully enabled in {} mode.", this.mode);
        }
    }

    @Bean
    public OkHttpClient okHttpClient(@Inject(required = false) MarkedClientEnabled marker) {
        if (marker == null) return null;
        val config = Solon.context().getBean(IClientProperties.class);
        return new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }
}
