package labs;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.smarthttp.SmHttpServer;
import org.noear.solon.core.bean.LifecycleBean;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpResponse;
import org.smartboot.http.server.HttpServerHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2023/4/6 created
 */
@Component
public class ServerDemo extends HttpServerHandler implements LifecycleBean {
    SmHttpServer _server;

    @Override
    public void start() throws Throwable {
        _server = new SmHttpServer();
        _server.setEnableWebSocket(false);
        _server.setCoreThreads(Runtime.getRuntime().availableProcessors() * 2);
        _server.setHandler(this); //如果使用 SmHttpContextHandler，则转发给 Solon.app()
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
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.write("Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}
