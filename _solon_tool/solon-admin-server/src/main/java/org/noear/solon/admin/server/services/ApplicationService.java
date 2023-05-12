package org.noear.solon.admin.server.services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.noear.solon.admin.server.data.Application;
import org.noear.solon.annotation.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ApplicationService {

    private final Set<Application> applications = new HashSet<>();

    public void registerApplication(Application application) {
        applications.add(application);
        log.info("Application registered: {}", application);
        // TODO: WebSocket push
    }

    public void unregisterApplication(Application application) {
        val find = applications.stream().filter(it -> it.equals(application)).findFirst();
        if (!find.isPresent()) return;
        applications.remove(find.get());
        log.info("Application unregistered: {}", find.get());
        // TODO: WebSocket push
    }

    public Set<Application> getApplications() {
        return new HashSet<>(applications);
    }

}
