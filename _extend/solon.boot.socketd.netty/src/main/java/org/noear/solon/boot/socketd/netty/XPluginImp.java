package org.noear.solon.boot.socketd.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.socketd.SessionFactoryManager;
import org.noear.solon.extend.socketd.SessionManager;

public class XPluginImp implements Plugin {
    ChannelFuture _server;

    public static String solon_boot_ver() {
        return "netty-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话工厂
        SessionManager.register(new _SessionManagerImpl());
        SessionFactoryManager.register(new _SessionFactoryImpl());

        if (app.enableSocket() == false) {
            return;
        }

        new Thread(() -> {
            start0(app);
        }).start();
    }

    private void start0(SolonApp app) {
        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: java.net.ServerSocket(netty-socketd)");

        int _port = app.cfg().getInt("server.socket.port", 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            //在服务器端的handler()方法表示对bossGroup起作用，而childHandler表示对wokerGroup起作用
            bootstrap.group(bossGroup, wokerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NioChannelInitializer(() -> new NioServerProcessor()));

            _server = bootstrap.bind(_port).sync();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: netty-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: netty-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {

            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();

            ex.printStackTrace();
        }
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.channel().close();
        _server = null;

        System.out.println("solon.Server:main: netty-socketd: Has Stopped " + solon_boot_ver());
    }
}
