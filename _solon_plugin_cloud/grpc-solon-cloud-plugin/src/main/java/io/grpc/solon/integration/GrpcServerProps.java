package io.grpc.solon.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.impl.BaseServerProps;

/**
 * @author noear
 * @since 1.8
 */
public class GrpcServerProps extends BaseServerProps {
    public static final String SERVER_GRPC_NAME = "server.grpc.name";
    public static final String SERVER_GRPC_PORT = "server.grpc.port";
    public static final String SERVER_GRPC_HOST = "server.grpc.host";
    public static final String SERVER_GRPC_CORETHREADS = "server.socket.coreThreads";
    public static final String SERVER_GRPC_MAXTHREADS = "server.socket.maxThreads";
    public static final String SERVER_GRPC_IDLETIMEOUT = "server.socket.idleTimeout";

    public GrpcServerProps(int portBase) {
        name = Solon.cfg().get(SERVER_GRPC_NAME);
        port = Solon.cfg().getInt(SERVER_GRPC_PORT, 0);
        host = Solon.cfg().get(SERVER_GRPC_HOST);

        if (port < 1) {
            port = portBase + Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }

        coreThreads = Solon.cfg().getInt(SERVER_GRPC_CORETHREADS, 0);
        maxThreads = Solon.cfg().getInt(SERVER_GRPC_MAXTHREADS, 0);
        idleTimeout = Solon.cfg().getLong(SERVER_GRPC_IDLETIMEOUT, 0L);
    }
}
