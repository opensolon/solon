package org.noear.solon.boot.jlhttp.test;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.jlhttp.HTTPServer;
import org.noear.solon.boot.jlhttp.JlHttpServer;
import org.noear.solon.core.bean.LifecycleBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * @author noear 2023/4/6 created
 */
@Component
public class ServerDemo implements LifecycleBean , HTTPServer.ContextHandler{
    JlHttpServer _server;
    @Override
    public void start() throws Throwable {
        _server = new JlHttpServer();
        _server.setExecutor(Executors.newCachedThreadPool());
        _server.setHandler(this); //如果使用 JlHttpContextHandler，则转发给 Solon.app()
        _server.start(null, Solon.cfg().serverPort() + 1);
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    @Override
    public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
        resp.getOutputStream().write("Hello world!".getBytes(StandardCharsets.UTF_8));
        return 0;
    }
}
