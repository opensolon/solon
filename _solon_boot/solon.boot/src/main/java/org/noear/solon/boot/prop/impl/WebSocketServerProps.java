package org.noear.solon.boot.prop.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.prop.ServerExecutorProps;
import org.noear.solon.boot.prop.ServerSignalProps;

/**
 * @author noear
 * @since 1.8
 */
public class WebSocketServerProps extends BaseServerProps implements ServerSignalProps, ServerExecutorProps {

    public WebSocketServerProps(int portBase) {
        name = Solon.cfg().get(ServerConstants.SERVER_WEBSOCKET_NAME);
        port = Solon.cfg().getInt(ServerConstants.SERVER_WEBSOCKET_PORT, 0);
        host = Solon.cfg().get(ServerConstants.SERVER_WEBSOCKET_HOST);

        if (port < 1) {
            port = portBase + Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }

        coreThreads = Solon.cfg().getInt(ServerConstants.SERVER_WEBSOCKET_CORETHREADS, 0);
        maxThreads = Solon.cfg().getInt(ServerConstants.SERVER_WEBSOCKET_MAXTHREADS, 0);
        idleTimeout = Solon.cfg().getLong(ServerConstants.SERVER_WEBSOCKET_IDLETIMEOUT, 0L);
    }
}