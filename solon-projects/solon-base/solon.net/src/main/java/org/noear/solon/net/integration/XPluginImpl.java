package org.noear.solon.net.integration;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.core.Listener;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.net.annotation.ClientEndpoint;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.socketd.SocketdRouter;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 2.6
 */
public class XPluginImpl implements Plugin {
    static final Logger log = LoggerFactory.getLogger(XPluginImpl.class);

    private final List<Client> socketdClientList = new ArrayList<>();

    private final SocketdRouter socketdRouter = SocketdRouter.getInstance();
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void start(AppContext context) throws Throwable {
        //注入容器
        context.wrapAndPut(WebSocketRouter.class, webSocketRouter);
        context.wrapAndPut(SocketdRouter.class, socketdRouter);

        //添加注解处理
        context.beanBuilderAdd(ServerEndpoint.class, this::serverEndpointBuild);
        context.beanBuilderAdd(ClientEndpoint.class, this::clientEndpointBuild);

        context.lifecycle(99, () -> {
            for (Client client : socketdClientList) {
                client.open();
            }
        });
    }

    private void serverEndpointBuild(Class<?> clz, BeanWrap bw, ServerEndpoint anno) {
        String path = Utils.annoAlias(anno.value(), anno.path());
        boolean registered = false;

        //socket.d
        if (bw.raw() instanceof Listener) {
            if (Utils.isEmpty(path)) {
                path = "**";
            }

            socketdRouter.of(path, bw.raw());
            registered = true;
        }

        //websocket
        if (bw.raw() instanceof WebSocketListener) {
            if (Utils.isEmpty(path)) {
                path = "**";
            }

            webSocketRouter.of(path, bw.raw());
            registered = true;
        }

        if (registered == false) {
            log.warn("@ServerEndpoint does not support type: {}", clz.getName());
        }
    }

    private void clientEndpointBuild(Class<?> clz, BeanWrap bw, ClientEndpoint anno) {
        if (bw.raw() instanceof Listener) {
            Client client = SocketD.createClient(anno.url());
            client.config(options -> options
                            .autoReconnect(anno.autoReconnect())
                            .heartbeatInterval(anno.heartbeatRate() * 1000))
                    .listen(bw.raw());

            socketdClientList.add(client);

            log.info("@ClientEndpoint socket.d listener registered: {}", clz.getName());
            return;
        }

        log.warn("@ClientEndpoint does not support type: {}", clz.getName());
    }
}