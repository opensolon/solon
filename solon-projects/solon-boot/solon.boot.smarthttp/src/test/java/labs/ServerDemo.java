package labs;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.smarthttp.SmHttpServer;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear 2023/4/6 created
 */
@Component
public class ServerDemo implements LifecycleBean , Handler {
    SmHttpServer _server;

    @Override
    public void start() throws Throwable {
        _server = new SmHttpServer();
        _server.enableWebSocket(false);
        _server.setCoreThreads(Runtime.getRuntime().availableProcessors() * 2);
        _server.setHandler(this); //如果使用 Solon.app()::tryHandle，则转发给 Solon.app()
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
    public void handle(Context ctx) throws Throwable {
        ctx.output("Hello world!");
    }
}
