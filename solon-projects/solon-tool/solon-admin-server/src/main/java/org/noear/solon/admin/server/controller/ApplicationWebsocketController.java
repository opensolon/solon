package org.noear.solon.admin.server.controller;

import com.google.gson.Gson;
import lombok.val;
import org.noear.solon.admin.server.data.ApplicationWebsocketTransfer;
import org.noear.solon.admin.server.services.ApplicationService;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.ServerEndpoint;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.util.List;

@ServerEndpoint(path = "/ws/application")
public class ApplicationWebsocketController implements Listener {

    @Inject
    private Gson gson;

    @Inject
    private ApplicationService applicationService;

    @Inject("applicationWebsocketSessions")
    private List<Session> sessions;

    @Override
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @Override
    public void onMessage(Session session, Message message) {
        val data = gson.fromJson(message.bodyAsString(), ApplicationWebsocketTransfer.class);

        if (data.getType().equals("getAllApplication")) {
            session.sendAsync(gson.toJson(new ApplicationWebsocketTransfer<>(
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
