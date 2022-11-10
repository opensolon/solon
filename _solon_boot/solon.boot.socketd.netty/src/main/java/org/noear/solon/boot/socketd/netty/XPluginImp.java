package org.noear.solon.boot.socketd.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.SocketServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.client.netty.NioChannelInitializer;

public class XPluginImp implements Plugin {
    private static Signal _signal;
    public static Signal signal(){
        return _signal;
    }

    ChannelFuture _server;

    public static String solon_boot_ver() {
        return "netty-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (Solon.app().enableSocketD() == false) {
            return;
        }

        context.beanOnloaded((ctx) -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) {
        //初始化属性
        ServerProps.init();

        long time_start = System.currentTimeMillis();

        LogUtil.global().info("Server:main: java.net.ServerSocket(netty-socketd)");

        SocketServerProps props = new SocketServerProps(20000);
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();


        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NioChannelInitializer(() -> new NioServerProcessor()));

            if(Utils.isEmpty(_host)) {
                _server = bootstrap.bind(_port).await();
            }else {
                _server = bootstrap.bind(_host, _port).await();
            }

            _signal = new SignalSim(_name, _host, _port, "tcp", SignalType.SOCKET);
            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            LogUtil.global().info("Connector:main: netty-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            LogUtil.global().info("Server:main: netty-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (RuntimeException e) {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();

            throw e;
        } catch (Throwable e) {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();

            throw new IllegalStateException(e);
        }
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.channel().close();
        _server = null;

        LogUtil.global().info("Server:main: netty-socketd: Has Stopped " + solon_boot_ver());
    }
}
