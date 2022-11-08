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
public class HttpServerProps extends BaseServerProps implements ServerSignalProps, ServerExecutorProps {
    public HttpServerProps() {
        name = Solon.cfg().get(ServerConstants.SERVER_HTTP_NAME);
        port = Solon.cfg().getInt(ServerConstants.SERVER_HTTP_PORT, 0);
        host = Solon.cfg().get(ServerConstants.SERVER_HTTP_HOST);

        if (port < 1) {
            port = Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }

        coreThreads = Solon.cfg().getInt(ServerConstants.SERVER_HTTP_CORETHREADS, 0);
        maxThreads = Solon.cfg().getInt(ServerConstants.SERVER_HTTP_MAXTHREADS, 0);
        idleTimeout = Solon.cfg().getLong(ServerConstants.SERVER_HTTP_IDLETIMEOUT, 0L);
    }
}
