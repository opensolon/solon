package org.noear.solon.admin.client.registration;

import org.noear.solon.admin.client.config.AdminClientBootstrapConfiguration;
import org.noear.solon.admin.client.config.IClientProperties;
import org.noear.solon.admin.client.services.ApplicationRegistrationService;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.AppPrestopEndEvent;
import org.noear.solon.core.event.EventBus;

import java.util.Timer;
import java.util.TimerTask;

@Configuration
public class AutoRegistrationConfiguration {

    private final Timer timer = new Timer();
    @Inject
    private IClientProperties clientProperties;

    @Bean
    public void afterInjection(
            @Inject(required = false) AdminClientBootstrapConfiguration.MarkedClientEnabled marked,
            @Inject ApplicationRegistrationService applicationRegistrationService
    ) {
        if (marked == null) return;
        EventBus.subscribe(AppLoadEndEvent.class, e -> onStart(applicationRegistrationService));
        EventBus.subscribe(AppPrestopEndEvent.class, e -> onStop(applicationRegistrationService));
    }

    public void onStart(ApplicationRegistrationService applicationRegistrationService) {
        applicationRegistrationService.register();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                applicationRegistrationService.heartbeat();
            }
        }, 0, clientProperties.getHeartbeatInterval());
    }

    public void onStop(ApplicationRegistrationService applicationRegistrationService) {
        timer.cancel();
    }
}
