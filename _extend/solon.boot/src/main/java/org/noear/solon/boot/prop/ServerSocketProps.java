package org.noear.solon.boot.prop;

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

    public ServerSocketProps(int portBase) {
        name = Solon.cfg().get(ServerConstants.SERVER_SOCKET_NAME);
        port = Solon.cfg().getInt(ServerConstants.SERVER_SOCKET_PORT, 0);
        host = Solon.cfg().get(ServerConstants.SERVER_SOCKET_HOST);

        if (port < 1) {
            port = portBase + Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }
    }
}
