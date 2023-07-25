package org.noear.solon.admin.server.controller;

import org.noear.solon.admin.server.data.ApplicationWebsocketTransfer;
import org.noear.solon.admin.server.services.ApplicationService;
import org.noear.solon.admin.server.services.ClientMonitorService;
import org.noear.solon.admin.server.utils.JsonUtils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.ServerEndpoint;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.util.List;

/**
 * 应用程序 WebSocket Controller
 *
 * @author shaokeyibb
 * @since 2.3
 */
@ServerEndpoint(path = "/ws/application")
public class ApplicationWebsocketController implements Listener {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ClientMonitorService clientMonitorService;

    @Inject("applicationWebsocketSessions")
    private List<Session> sessions;

    @Override
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @Override
    public void onMessage(Session session, Message message) {
        ApplicationWebsocketTransfer data = JsonUtils.fromJson(message.bodyAsString(), ApplicationWebsocketTransfer.class);

        // 获取全部应用程序信息
        if (data.getType().equals("getAllApplication")) {
            session.sendAsync(JsonUtils.toJson(new ApplicationWebsocketTransfer<>(
                    null,
                    "getAllApplication",
                    applicationService.getApplications()
            )));
        }
    }

    @Override
    public void onClose(Session session) {
        sessions.remove(session);
    }
}
