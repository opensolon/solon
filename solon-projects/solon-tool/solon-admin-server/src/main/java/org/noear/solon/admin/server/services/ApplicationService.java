package org.noear.solon.admin.server.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.noear.solon.admin.server.config.ServerProperties;
import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.data.ApplicationWebsocketTransfer;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.message.Session;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ApplicationService {

    private final Set<Application> applications = new HashSet<>();

    private final Map<Application, Runnable> runningHeartbeatTasks = new HashMap<>();

    @Inject
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Inject
    private ServerProperties serverProperties;

    @Inject("applicationWebsocketSessions")
    private List<Session> sessions;

    @Inject
    private Gson gson;

    public void registerApplication(Application application) {
        if (applications.contains(application)) return;

        applications.add(application);
        scheduleHeartbeatCheck(application);
        sessions.forEach(it -> it.sendAsync(gson.toJson(new ApplicationWebsocketTransfer<>(
                "registerApplication",
                application
        ))));
        log.info("Application registered: {}", application);
    }

    public void unregisterApplication(Application application) {
        val find = applications.stream().filter(it -> it.equals(application)).findFirst();
        if (!find.isPresent()) return;

        applications.remove(find.get());
        scheduledThreadPoolExecutor.remove(runningHeartbeatTasks.get(find.get()));
        sessions.forEach(it -> it.sendAsync(gson.toJson(new ApplicationWebsocketTransfer<>(
                "unregisterApplication",
                find.get()
        ))));
        log.info("Application unregistered: {}", find.get());
    }

    public void heartbeatApplication(Application application) {
        val find = applications.stream().filter(it -> it.equals(application)).findFirst();
        if (!find.isPresent()) return;
        find.get().setLastHeartbeat(System.currentTimeMillis());

        if (application.getStatus() == Application.Status.UP) return;

        find.get().setStatus(Application.Status.UP);

        find.get().setLastUpTime(System.currentTimeMillis());

        sessions.forEach(it -> it.sendAsync(gson.toJson(new ApplicationWebsocketTransfer<>(
                "updateApplication",
                find.get()
        ))));

        log.debug("Application heartbeat: {}", find.get());
    }

    private void scheduleHeartbeatCheck(Application application) {
        Runnable heartbeatCallback = () -> {
            runHeartbeatCheck(application);
            scheduleHeartbeatCheck(application);
        };
        runningHeartbeatTasks.put(application, heartbeatCallback);
        scheduledThreadPoolExecutor.schedule(heartbeatCallback, serverProperties.getHeartbeatInterval(), TimeUnit.MILLISECONDS);
    }

    private void runHeartbeatCheck(Application application) {
        if (System.currentTimeMillis() - application.getLastHeartbeat() <= serverProperties.getHeartbeatInterval())
            return;

        if (application.getStatus() == Application.Status.DOWN) return;

        application.setStatus(Application.Status.DOWN);

        application.setLastDownTime(System.currentTimeMillis());

        sessions.forEach(it -> it.sendAsync(gson.toJson(new ApplicationWebsocketTransfer<>(
                "updateApplication",
                application
        ))));
    }

    public Set<Application> getApplications() {
        return new HashSet<>(applications);
    }

    public Application getApplication(String name, String baseUrl) {
        val find = applications.stream().filter(it -> it.getName().equals(name) && it.getBaseUrl().equals(baseUrl)).findFirst();
        return find.orElse(null);
    }

}
