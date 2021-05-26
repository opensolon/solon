package org.noear.solon.boot.socketd.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.client.netty.NioChannelInitializer;

public class XPluginImp implements Plugin {
    ChannelFuture _server;

    public static String solon_boot_ver() {
        return "netty-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (app.enableSocketD() == false) {
            return;
        }

        new Thread(() -> {
            start0(app);
        }).start();
    }

    private void start0(SolonApp app) {
        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: java.net.ServerSocket(netty-socketd)");

        String _name = app.cfg().get("server.socket.name");
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


            app.signalAdd(new SignalSim(_name, _port, "tcp", SignalType.SOCKET));

            long time_end = System.currentTimeMillis();

            PrintUtil.info("solon.connector:main: netty-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: netty-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {

            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();

            EventBus.push(ex);
        }
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.channel().close();
        _server = null;

        PrintUtil.info("Server:main: netty-socketd: Has Stopped " + solon_boot_ver());
    }
}
