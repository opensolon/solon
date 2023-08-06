package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.boot.jdkhttp.JdkHttpServer;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.concurrent.Executors;

/**
 * @author noear 2023/4/6 created
 */
@Component
public class ServerDemo implements LifecycleBean , Handler {
    JdkHttpServer _server;

    @Override
    public void start() throws Throwable {
        _server = new JdkHttpServer();
        _server.setExecutor(Executors.newCachedThreadPool());
        _server.setHandler(this); //如果使用 Solon.app()::tryHandle，则转发给 Solon.app()
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
    public void handle(Context ctx) throws Throwable {
        ctx.output("Hello world!");
    }
}
