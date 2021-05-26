package org.noear.solon.boot.undertow;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.util.PrintUtil;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class XPluginImp implements Plugin {
    private Plugin _server = null;


    public static String solon_boot_ver(){
        return "undertow 2.1/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        if (app.enableHttp() == false) {
            return;
        }

        Aop.context().beanBuilderAdd(WebFilter.class,(clz,bw,ano)->{});
        Aop.context().beanBuilderAdd(WebServlet.class,(clz, bw, ano)->{});
        Aop.context().beanBuilderAdd(WebListener.class,(clz, bw, ano)->{});

        XServerProp.init();

        Aop.beanOnloaded(() -> {
            start0(app);
        });
    }

    private void start0(SolonApp app) {
        String _name = app.cfg().get("server.http.name");
        int _port = app.cfg().getInt("server.http.port", 0);
        if (_port < 1) {
            _port = app.port();
        }

        long time_start = System.currentTimeMillis();
        PrintUtil.info("Server:main: Undertow 2.1.09(undertow)");

        Class<?> jspClz = Utils.loadClass("io.undertow.jsp.JspServletBuilder");

        if (jspClz == null) {
            _server = new PluginUndertow(_port);
        } else {
            _server = new PluginUndertowJsp(_port);
        }

        _server.start(app);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "solon.connector:main: undertow: Started ServerConnector@{HTTP/1.1,[http/1.1]";
        if (app.enableWebSocket()) {
            System.out.println(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
        } else {
            System.out.println(connectorInfo + "}{0.0.0.0:" + _port + "}");
        }

        app.signalAdd(new SignalSim(_name, _port, "http", SignalType.HTTP));

        PrintUtil.info("Server:main: undertow: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            PrintUtil.info("Server:main: undertow: Has Stopped " + solon_boot_ver());
        }
    }
}
