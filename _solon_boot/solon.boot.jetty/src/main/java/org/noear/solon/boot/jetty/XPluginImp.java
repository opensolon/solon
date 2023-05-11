package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.http.FormContentFilter;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private JettyServerBase _server = null;


    public static String solon_boot_ver() {
        return "jetty 9.4.51/" + Solon.version();
    }


    @Override
    public void start(AopContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        if (ServerProps.request_maxBodySize != 0) {
            System.setProperty(ContextHandler.MAX_FORM_CONTENT_SIZE_KEY,
                    String.valueOf(ServerProps.request_maxBodySize));
        }

        context.beanBuilderAdd(WebFilter.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebServlet.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebListener.class, (clz, bw, ano) -> {
        });

        context.lifecycle(-99, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        Class<?> jspClz = ClassUtil.loadClass("org.eclipse.jetty.jsp.JettyJspServlet");


        if (jspClz == null) {
            _server = new JettyServer();
        } else {
            _server = new JettyServerAddJsp();
        }

        HttpServerProps props = _server.getProps();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

        EventBus.push(_server);
        _server.start(_host, _port);


        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "solon.connector:main: jetty: Started ServerConnector@{HTTP/1.1,[http/1.1]";
        if (app.enableWebSocket()) {
            System.out.println(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
        }

        System.out.println(connectorInfo + "}{http://localhost:" + _port + "}");

        LogUtil.global().info("Server:main: jetty: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");

        app.before(-9, new FormContentFilter());
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            LogUtil.global().info("Server:main: jetty: Has Stopped (" + solon_boot_ver() + ")");
        }
    }
}
