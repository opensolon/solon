package org.noear.solon.boot.jdkhttp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

public final class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    public static String solon_boot_ver() {
        return "jdk http/" + Solon.version();
    }

    JdkHttpServerComb _server;

    @Override
    public void start(AopContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        //如果有jetty插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.jetty.XPluginImp") != null) {
            return;
        }

        //如果有undrtow插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.undertow.XPluginImp") != null) {
            return;
        }

        //如果有smarthttp插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.smarthttp.XPluginImp") != null) {
            return;
        }

        context.lifecycle(-99, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        HttpServerProps props = new HttpServerProps();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();


        _server = new JdkHttpServerComb();
        _server.setExecutor(props.getBioExecutor("jdkhttp-"));
        _server.setHandler(Solon.app()::tryHandle);

        //尝试事件扩展
        EventBus.push(_server);
        _server.start(_host, _port);


        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: jdkhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
        LogUtil.global().info("Server:main: jdkhttp: Started ("+solon_boot_ver()+") @" + (time_end - time_start) + "ms");
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;
        LogUtil.global().info("Server:main: jdkhttp: Has Stopped (" + solon_boot_ver()+")");
    }
}
