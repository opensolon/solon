package org.noear.solon.extend.grpc.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerSignalProps;

/**
 * @author noear
 * @since 1.8
 */
public class GrpcSignalProps implements ServerSignalProps {
    public static final String SERVER_GRPC_NAME = "server.grpc.name";
    public static final String SERVER_GRPC_PORT = "server.grpc.port";
    public static final String SERVER_GRPC_HOST = "server.grpc.host";

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

    public GrpcSignalProps(int portBase) {
        name = Solon.cfg().get(SERVER_GRPC_NAME);
        port = Solon.cfg().getInt(SERVER_GRPC_PORT, 0);
        host = Solon.cfg().get(SERVER_GRPC_HOST);

        if (port < 1) {
            port = portBase + Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }
    }
}
