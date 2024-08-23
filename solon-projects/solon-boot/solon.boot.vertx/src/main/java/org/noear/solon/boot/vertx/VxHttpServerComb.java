package org.noear.solon.boot.vertx;

import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.http.HttpServerConfigure;
import org.noear.solon.core.handle.Handler;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author noear
 * @since 2.9
 */
public class VxHttpServerComb implements HttpServerConfigure, ServerLifecycle {
    private int coreThreads;
    private Executor workExecutor;
    private boolean enableWebSocket;
    private Handler handler;
    protected boolean enableSsl = true;
    protected SSLContext sslContext;
    protected boolean enableDebug = false;
    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();
    protected List<VxHttpServer> servers = new ArrayList<>();

    @Override
    public void enableSsl(boolean enable, SSLContext sslContext) {
        this.enableSsl = enable;
        this.sslContext = sslContext;
    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    @Override
    public void addHttpPort(int port) {
        addHttpPorts.add(port);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.workExecutor = executor;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    public boolean isSecure() {
        if (servers.size() > 0) {
            return servers.get(0).isSecure();
        } else {
            return false;
        }
    }

    @Override
    public void start(String host, int port) throws Throwable {
        {
            VxHttpServer s1 = new VxHttpServer();
            s1.setWorkExecutor(workExecutor);
            s1.setCoreThreads(coreThreads);
            s1.enableWebSocket(enableWebSocket);
            s1.setHandler(handler);
            s1.enableSsl(enableSsl, sslContext);
            s1.enableDebug(enableDebug);
            s1.start(host, port);

            servers.add(s1);
        }

        for (Integer portAdd : addHttpPorts) {
            VxHttpServer s2 = new VxHttpServer();
            s2.setWorkExecutor(workExecutor);
            s2.setCoreThreads(coreThreads);
            s2.enableWebSocket(enableWebSocket);
            s2.setHandler(handler);
            s2.enableSsl(false, null); //只支持http
            s2.enableDebug(enableDebug);
            s2.start(host, portAdd);

            servers.add(s2);
        }
    }

    @Override
    public void stop() throws Throwable {
        for (ServerLifecycle s : servers) {
            s.stop();
        }
    }
}
