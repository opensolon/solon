package org.noear.solon.net.integration;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.server.ServerConfigHandler;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.impl.SocketServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.annotation.ClientEndpoint;
import org.noear.solon.net.annotation.ServerEndpoint;
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

    List<Server> socketdServerList = new ArrayList<>();
    List<Client> socketdClientList = new ArrayList<>();

    WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void start(AppContext context) throws Throwable {
        //注入容器
        context.wrapAndPut(WebSocketRouter.class, webSocketRouter);

        //添加注解处理
        context.beanBuilderAdd(ServerEndpoint.class, this::serverEndpointBuild);
        context.beanBuilderAdd(ClientEndpoint.class, this::clientEndpointBuild);

        //构建生命事件
        context.lifecycle(-99, () -> {
            for (Server server : socketdServerList) {
                long time_start = System.currentTimeMillis();

                server.start();

                long time_end = System.currentTimeMillis();
                LogUtil.global().info("Connector:main: socket.d: Started ServerConnector@{[" + server.config().getSchema() + "]}{0.0.0.0:" + server.config().getPort() + "}");
                LogUtil.global().info("Server:main: socket.d: Started (" + server.title() + ") @" + (time_end - time_start) + "ms");
            }
        });

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
            if (Utils.isEmpty(anno.schema())) {
                throw new IllegalStateException("Socket.D listener need to specify the schema");
            }

            SocketServerProps props = new SocketServerProps(20000);

            Server server = SocketD.createServer(anno.schema());
            server.config(c -> {
                c.port(props.getPort());
                c.host(props.getHost());
                c.coreThreads(props.getCoreThreads());
                c.maxThreads(props.getMaxThreads(true));
            });
            server.listen(bw.raw());
            if (bw.raw() instanceof ServerConfigHandler) {
                server.config(bw.raw());
            }

            socketdServerList.add(server);

            //登记信号
            final String _wrapHost = props.getWrapHost();
            final int _wrapPort = props.getWrapPort();
            Signal _signal = new SignalSim(props.getName(), _wrapHost, _wrapPort, anno.schema(), SignalType.SOCKET);

            Solon.app().signalAdd(_signal);

            registered = true;
        }

        //websocket
        if (bw.raw() instanceof WebSocketListener) {
            if (Utils.isEmpty(path)) {
                path = "**";
            }

            webSocketRouter.main(path, 1, bw.raw());
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

    @Override
    public void stop() throws Throwable {
        for (Server server : socketdServerList) {
            RunUtil.runAndTry(server::stop);
        }
    }
}