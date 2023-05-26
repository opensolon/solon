package org.noear.solon.boot.jlhttp.test;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.boot.jlhttp.JlHttpServer;
import org.noear.solon.core.bean.LifecycleBean;

import java.util.concurrent.Executors;

/**
 * @author noear 2023/4/6 created
 */
@SolonMain
public class ServerDemo {

    public static void main(String[] args) {
        Solon.start(ServerDemo.class, args, app -> {
            if (app.cfg().argx().containsKey("ssl")) {
                app.context().lifecycle(new ServerImpl());
            }
        });
    }

    public static class ServerImpl implements LifecycleBean {
        JlHttpServer _server;

        @Override
        public void start() throws Throwable {
            _server = new JlHttpServer();
            _server.setExecutor(Executors.newCachedThreadPool());
            _server.setHandler(Solon.app()::tryHandle); //如果使用 Solon.app()::tryHandle，则转发给 Solon.app()
            _server.allowSsl(false);
            _server.start(null, Solon.cfg().serverPort() + 1);
        }

        @Override
        public void stop() throws Throwable {
            if (_server != null) {
                _server.stop();
                _server = null;
            }
        }
    }
}
