package org.noear.solon.boot.socketd;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.server.Server;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;

/**
 * @author noear
 * @since 2.6
 */
public class SdSocket implements ServerLifecycle {
    private String schema;
    private Listener listener;

    private Server server;

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        if (schema == null) {
            schema = "sd:tcp";
        }

        long time_start = System.currentTimeMillis();

        server = SocketD.createServer(schema)
                .config(c -> c.host(host).port(port))
                .listen(listener);

        //事件总线扩展
        EventBus.publish(server);

        server.start();

        long time_end = System.currentTimeMillis();
        LogUtil.global().info("Connector:main: socket.d: Started ServerConnector@{[" + server.config().getSchema() + "]}{0.0.0.0:" + server.config().getPort() + "}");
        LogUtil.global().info("Server:main: socket.d: Started (" + server.title() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.stop();
        }
    }
}
