package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.http.FormContentFilter;
import org.noear.solon.core.*;
import org.noear.solon.core.util.PrintUtil;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class XPluginImp implements Plugin {
    private static Signal _signal;
    public static Signal signal(){
        return _signal;
    }

    private PluginJetty _server = null;


    public static String solon_boot_ver(){
        return "jetty 9.4/" + Solon.cfg().version();
    }


    @Override
    public void start(AopContext context) {
        if (Solon.global().enableHttp() == false) {
            return;
        }

        if (ServerProps.request_maxBodySize != 0) {
            System.setProperty(ContextHandler.MAX_FORM_CONTENT_SIZE_KEY,
                    String.valueOf(ServerProps.request_maxBodySize));
        }

        context.beanBuilderAdd(WebFilter.class,(clz, bw, ano)->{});
        context.beanBuilderAdd(WebServlet.class,(clz, bw, ano)->{});
        context.beanBuilderAdd(WebListener.class,(clz, bw, ano)->{});

        context.beanOnloaded((ctx) -> {
            start0(Solon.global(), context);
        });
    }

    private void start0(SolonApp app, AopContext context) {
        Class<?> jspClz = Utils.loadClass("org.eclipse.jetty.jsp.JettyJspServlet");


        String _name = app.cfg().get(ServerConstants.SERVER_HTTP_NAME);
        int _port = app.cfg().getInt(ServerConstants.SERVER_HTTP_PORT, 0);
        if (_port < 1) {
            _port = app.port();
        }


        if (jspClz == null) {
            _server = new PluginJetty(_port);
        } else {
            _server = new PluginJettyJsp(_port);
        }

        long time_start = System.currentTimeMillis();
        PrintUtil.info("Server:main: Jetty 9.4(jetty)");

        _server.start(context);
        _signal = new SignalSim(_name, _port, "http", SignalType.HTTP);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "solon.connector:main: jetty: Started ServerConnector@{HTTP/1.1,[http/1.1]";
        if (app.enableWebSocket()) {
            System.out.println(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
        }

        System.out.println(connectorInfo + "}{http://localhost:" + _port + "}");

        PrintUtil.info("Server:main: jetty: Started @" + (time_end - time_start) + "ms");

        app.before(-9, new FormContentFilter());
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            PrintUtil.info("Server:main: jetty: Has Stopped " + solon_boot_ver());
        }
    }
}
