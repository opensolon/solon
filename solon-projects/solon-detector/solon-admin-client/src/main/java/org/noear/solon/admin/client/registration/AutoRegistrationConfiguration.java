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
package org.noear.solon.admin.client.registration;

import org.noear.solon.admin.client.config.AdminClientBootstrapConfiguration;
import org.noear.solon.admin.client.config.ClientProperties;
import org.noear.solon.admin.client.services.ApplicationRegistrationService;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.AppPrestopEndEvent;
import org.noear.solon.core.event.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自动向 Solon Admin Server 注册客户端信息
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Configuration
public class AutoRegistrationConfiguration {

    private final Timer timer = new Timer();
    @Inject
    private ClientProperties clientProperties;

    @Bean
    public void afterInjection(
            @Inject(required = false) AdminClientBootstrapConfiguration.MarkedClientEnabled marked,
            @Inject ApplicationRegistrationService applicationRegistrationService
    ) {
        if (marked == null) return;

        // 订阅事件
        EventBus.subscribe(AppLoadEndEvent.class, e -> onStart(applicationRegistrationService));
        EventBus.subscribe(AppPrestopEndEvent.class, e -> onStop(applicationRegistrationService));
    }

    public void onStart(ApplicationRegistrationService applicationRegistrationService) {
        // 注册应用程序
        applicationRegistrationService.register();

        // 计划心跳
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                applicationRegistrationService.heartbeat();
            }
        }, 0, clientProperties.getHeartbeatInterval());
    }

    public void onStop(ApplicationRegistrationService applicationRegistrationService) {
        // 取消心跳计时器
        timer.cancel();
    }
}
