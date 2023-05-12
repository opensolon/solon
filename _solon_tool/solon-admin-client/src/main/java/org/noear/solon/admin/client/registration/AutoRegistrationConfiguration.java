package org.noear.solon.admin.client.registration;

import lombok.SneakyThrows;
import org.noear.solon.admin.client.config.AdminClientBootstrapConfiguration;
import org.noear.solon.admin.client.services.ApplicationRegistrationService;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.AppPrestopEndEvent;
import org.noear.solon.core.event.EventBus;

@Configuration
public class AutoRegistrationConfiguration {

    @Bean
    public void afterInjection(
            @Inject(required = false) AdminClientBootstrapConfiguration.MarkedClientEnabled marked,
            @Inject ApplicationRegistrationService applicationRegistrationService
    ) {
        if (marked == null) return;
        EventBus.subscribe(AppLoadEndEvent.class, e -> onStart(applicationRegistrationService));
        EventBus.subscribe(AppPrestopEndEvent.class, e -> onStop(applicationRegistrationService));
    }

    @SneakyThrows
    public void onStart(ApplicationRegistrationService applicationRegistrationService) {
        applicationRegistrationService.register();
    }

    @SneakyThrows
    public void onStop(ApplicationRegistrationService applicationRegistrationService) {
        applicationRegistrationService.unregister();
    }
}
