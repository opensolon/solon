package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.jdkhttp.JdkHttpContextHandler;
import org.noear.solon.boot.jdkhttp.JdkHttpServer;
import org.noear.solon.core.bean.LifecycleBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * @author noear 2023/4/6 created
 */
@Component
public class ServerDemo implements LifecycleBean , HttpHandler {
    JdkHttpServer _server;

    @Override
    public void start() throws Throwable {
        _server = new JdkHttpServer();
        _server.setExecutor(Executors.newCachedThreadPool());
        _server.setHandler(this); //如果使用 JdkHttpContextHandler，则转发给 Solon.app()
        _server.start(null, 8801);
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseBody().write("Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}
