package org.noear.solon.boot.smarthttp;

import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.http.HttpServerConfigure;
import org.noear.solon.core.handle.Handler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 通过组合支持多端口模式
 *
 * @author noear
 * @since 2.2
 */
public class SmHttpServerComb implements HttpServerConfigure, ServerLifecycle {
    private int coreThreads;
    private Executor workExecutor;
    private boolean enableWebSocket;
    private Handler handler;
    protected boolean allowSsl = true;
    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();
    protected List<SmHttpServer> servers = new ArrayList<>();

    /**
     * 是否允许Ssl
     */
    @Override
    public void allowSsl(boolean allowSsl) {
        this.allowSsl = allowSsl;
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

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public void setWorkExecutor(Executor workExecutor) {
        this.workExecutor = workExecutor;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        {
            SmHttpServer s1 = new SmHttpServer();
            s1.setWorkExecutor(workExecutor);
            s1.setCoreThreads(coreThreads);
            s1.enableWebSocket(enableWebSocket);
            s1.setHandler(handler);
            s1.allowSsl(allowSsl);
            s1.start(host, port);

            servers.add(s1);
        }

        for (Integer portAdd : addHttpPorts) {
            SmHttpServer s2 = new SmHttpServer();
            s2.setWorkExecutor(workExecutor);
            s2.setCoreThreads(coreThreads);
            s2.enableWebSocket(enableWebSocket);
            s2.setHandler(handler);
            s2.allowSsl(false); //只支持http
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
