package org.noear.solon.admin.client.config;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;

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

    @Inject
    AppContext appContext;

    @Inject(value = "${solon.admin.client}", required = false)
    ClientProperties clientProperties = new ClientProperties();

    @Bean
    public ClientProperties clientProperties() {
        if ("local".equals(clientProperties.getMode())) {
            log.debug("Injected localClientProperties: " + clientProperties);
        } else {
            log.debug("Injected cloudClientProperties: " + clientProperties);
        }

        String serverUrl = (String) Solon.app().shared().get("solon-admin-server-url");
        if (Utils.isNotEmpty(serverUrl)) {
            clientProperties.setServerUrl(serverUrl);
        }

        return clientProperties;
    }

    @Bean
    public MarkedClientEnabled markedClientEnabled(@Inject ClientProperties clientProperties) {
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
    public OkHttpClient okHttpClient(@Inject(required = false) MarkedClientEnabled marker,
                                     @Inject ClientProperties clientProperties) {
        if (marker == null) return null;

        return new OkHttpClient.Builder()
                .connectTimeout(clientProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(clientProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }
}
