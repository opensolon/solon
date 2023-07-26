package org.noear.solon.admin.server.services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.noear.solon.admin.server.config.ServerProperties;
import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.data.ApplicationWebsocketTransfer;
import org.noear.solon.admin.server.utils.JsonUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.message.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shaokeyibb
 * @since 2.3
 */
@Slf4j
@Component
public class ApplicationService {

    private final Map<String, Application> applications = new HashMap<>();

    private final Map<Application, Runnable> runningHeartbeatTasks = new HashMap<>();

    private final Map<Application, Runnable> runningClientMonitorTasks = new HashMap<>();

    @Inject
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Inject
    private ServerProperties serverProperties;

    @Inject("applicationWebsocketSessions")
    private List<Session> sessions;

    @Inject
    private ClientMonitorService clientMonitorService;

    /**
     * 注册 Solon Admin Client 应用程序
     *
     * @param application 应用程序
     */
    public void registerApplication(Application application) {
        String key = application.toKey();
        Application persisted = applications.get(key);
        if (persisted != null) { // on app restart, update app info
            persisted.replace(application);
            return;
        }

        applications.put(key, application);
        // 计划心跳检测
        scheduleHeartbeatCheck(application);
        // 向前端发送注册信息
        sessions.forEach(it -> it.sendAsync(JsonUtils.toJson(new ApplicationWebsocketTransfer<>(
                null,
                "registerApplication",
                application
        ))));

        // 计划客户端监视器数据获取
        scheduleClientMonitor(application);

        log.info("Application registered: {}", application);
    }

    /**
     * 注销 Solon Admin Client 应用程序
     *
     * @param application 应用程序
     */
    public void unregisterApplication(Application application) {
        val find = applications.values().stream().filter(it -> it.equals(application)).findFirst();
        if (!find.isPresent()) return;

        applications.remove(find.get().toKey());
        scheduledThreadPoolExecutor.remove(runningHeartbeatTasks.get(find.get()));
        scheduledThreadPoolExecutor.remove(runningClientMonitorTasks.get(find.get()));
        // 向前端发送注销信息
        sessions.forEach(it -> it.sendAsync(JsonUtils.toJson(new ApplicationWebsocketTransfer<>(
                null,
                "unregisterApplication",
                find.get()
        ))));

        log.info("Application unregistered: {}", find.get());
    }

    /**
     * 心跳
     *
     * @param application 应用程序
     */
    public void heartbeatApplication(Application application) {
        val find = applications.values().stream().filter(it -> it.equals(application)).findFirst();
        if (!find.isPresent()) return;
        find.get().setLastHeartbeat(System.currentTimeMillis());

        // 如果应用程序已经是 UP 状态，则不需要再次发送心跳
        if (application.getStatus() == Application.Status.UP) return;

        // 更新应用程序状态
        find.get().setStatus(Application.Status.UP);
        find.get().setLastUpTime(System.currentTimeMillis());

        // 向前端发送数据更新
        sessions.forEach(it -> it.sendAsync(JsonUtils.toJson(new ApplicationWebsocketTransfer<>(
                null,
                "updateApplication",
                find.get()
        ))));

        log.trace("Application heartbeat: {}", find.get());
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

        // 如果应用程序已经是 DOWN 状态，则不需要再次发送心跳
        if (application.getStatus() == Application.Status.DOWN) return;

        // 更新应用程序状态
        application.setStatus(Application.Status.DOWN);
        application.setLastDownTime(System.currentTimeMillis());

        // 向前端发送数据更新
        sessions.forEach(it -> it.sendAsync(JsonUtils.toJson(new ApplicationWebsocketTransfer<>(
                null,
                "updateApplication",
                application
        ))));
    }

    private void scheduleClientMonitor(Application application) {
        Runnable clientMonitorCallback = () -> {
            runClientMonitor(application);
            scheduleClientMonitor(application);
        };
        runningClientMonitorTasks.put(application, clientMonitorCallback);
        scheduledThreadPoolExecutor.schedule(clientMonitorCallback, serverProperties.getClientMonitorPeriod(), TimeUnit.MILLISECONDS);
    }

    private void runClientMonitor(Application application) {
        application.setMonitors(clientMonitorService.getMonitors(application));

        // 向前端发送数据更新
        sessions.forEach(it -> it.sendAsync(JsonUtils.toJson(new ApplicationWebsocketTransfer<>(
                null,
                "updateApplication",
                application
        ))));
    }

    /**
     * 获取全部应用程序
     *
     * @return 全部应用程序
     */
    public Collection<Application> getApplications() {
        return applications.values();
    }

    /**
     * 获取应用程序
     *
     * @param name    应用程序名称
     * @param baseUrl 应用程序 baseUrl
     * @return 应用程序
     */
    public Application getApplication(String name, String baseUrl) {
        val find = applications.values().stream().filter(it -> it.getName().equals(name) && it.getBaseUrl().equals(baseUrl)).findFirst();
        return find.orElse(null);
    }

}
