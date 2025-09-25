package org.noear.solon.server.grizzly;

import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.noear.solon.Solon;
import org.noear.solon.core.util.Assert;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.grizzly.http.GyHttpContextHandler;
import org.noear.solon.server.prop.impl.HttpServerProps;

import java.util.concurrent.ThreadFactory;

/**
 *
 * @author noear 2025/9/5 created
 *
 */
public class GyHttpServer implements ServerLifecycle {
    private HttpServer httpServer;
    private HttpServerProps  props;

    public GyHttpServer(HttpServerProps props) {
        this.props = props;
    }

    public boolean isSecure(){
        return false;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        if(Assert.isEmpty(host)){
            host = "0.0.0.0";
        }

        ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.defaultConfig();
        threadPoolConfig.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("Grizzly-Worker-" + thread.getId());
            return thread;
        });

        NetworkListener networkListener = new NetworkListener("solon", host, port);
        networkListener.getTransport().setWorkerThreadPoolConfig(threadPoolConfig);

        httpServer = new HttpServer();

        httpServer.addListener(networkListener);

        httpServer.getServerConfiguration().addHttpHandler(new GyHttpContextHandler(Solon.app()::tryHandle));

        httpServer.start();
    }

    @Override
    public void stop() throws Throwable {
        httpServer.shutdownNow();
    }
}