package org.noear.solon.boot.props;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

/**
 * @author noear
 * @since 1.8
 */
public class ServerSocketProps {
    private String name;
    private int port;
    private String host;

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public ServerSocketProps() {
        name = Solon.cfg().get(ServerConstants.SERVER_SOCKET_NAME);
        port = Solon.cfg().getInt(ServerConstants.SERVER_SOCKET_PORT, 0);
        host = Solon.cfg().get(ServerConstants.SERVER_SOCKET_HOST);

        if (port < 1) {
            port = 20000 + Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }
    }
}
