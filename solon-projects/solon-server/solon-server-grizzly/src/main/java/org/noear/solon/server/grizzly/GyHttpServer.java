package org.noear.solon.server.grizzly;

import org.glassfish.grizzly.http.server.*;
import com.icoderoad.GrizzlyServletWebServerFactory;
import org.noear.solon.server.ServerLifecycle;

/**
 *
 * @author noear 2025/9/5 created
 *
 */
public class GyHttpServer implements ServerLifecycle {
    HttpServer httpServer;

    @Override
    public void start(String host, int port) throws Throwable {
        httpServer = new HttpServer();

        httpServer.addListener(new NetworkListener("", "", 8080));

        httpServer.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) throws Exception {

            }
        });

        httpServer.start();

        GrizzlyServletWebServerFactory xxx;
    }

    @Override
    public void stop() throws Throwable {
        httpServer.shutdownNow();
    }
}