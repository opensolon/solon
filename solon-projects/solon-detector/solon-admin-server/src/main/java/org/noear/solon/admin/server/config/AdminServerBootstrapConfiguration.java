/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.admin.server.config;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 配置文件检查和注入
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Slf4j
@Configuration
public class AdminServerBootstrapConfiguration {

    @Condition(onProperty = "${solon.admin.server.enabled:true} = true")
    @Bean
    public MarkedServerEnabled markedServerEnabled(@Inject("${solon.admin.server.mode:local}") String mode) {
        return new MarkedServerEnabled(mode);
    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor(@Inject(required = false) MarkedServerEnabled marker) {
        if (marker == null) return null;
        return new ScheduledThreadPoolExecutor(1);
    }

    @Value
    public static class MarkedServerEnabled {
        String mode;

        public MarkedServerEnabled(String mode) {
            this.mode = mode;

            log.info("Solon Admin server has been successfully enabled in {} mode.", this.mode);
        }
    }

    @Bean
    public OkHttpClient okHttpClient(@Inject(required = false) MarkedServerEnabled marker) {
        if (marker == null) return null;
        val config = Solon.context().getBean(ServerProperties.class);

        return new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

}
