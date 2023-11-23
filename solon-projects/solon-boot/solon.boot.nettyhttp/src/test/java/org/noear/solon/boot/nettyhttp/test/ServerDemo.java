package org.noear.solon.boot.nettyhttp.test;

import java.net.InetSocketAddress;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.boot.nettyhttp.NettyHttpServer;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.bean.LifecycleBean;

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

            app.get("/get", c -> {
                c.output("htllo");
            });
        });
    }

    public static class ServerImpl implements LifecycleBean {

        NettyHttpServer _server;

        @Override
        public void start() throws Throwable {
            _server = new NettyHttpServer(new InetSocketAddress(Solon.cfg().serverPort()),
                    HttpServerProps.getInstance(), Solon.app()::tryHandle);
            _server.start(null, Solon.cfg().serverPort());
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
