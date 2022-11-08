package org.noear.solon.boot.tomcat;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class XPluginImp implements Plugin {
    public static final String TOMCAT_VER = "tomcat 9.0.64";
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private ServerLifecycle _server = null;

    public static String solon_boot_ver() {
        return TOMCAT_VER + "/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        context.beanBuilderAdd(WebFilter.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebServlet.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebListener.class, (clz, bw, ano) -> {
        });

        context.beanOnloaded((ctx) -> {
            try {
                start0(Solon.app());
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        Class<?> jspClz = Utils.loadClass("org.apache.jasper.servlet.JspServlet");

        HttpServerProps props = new HttpServerProps();
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        if (jspClz == null) {
            _server = new TomcatServer();
        } else {
            _server = new TomcatServerAddJsp();
        }

        long time_start = System.currentTimeMillis();
        LogUtil.global().info("Server:main: " + TOMCAT_VER + "(tomcat)");

        _server.start(_host, _port);
        _signal = new SignalSim(_name, _host, _port, "http", SignalType.HTTP);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "solon.connector:main: tomcat: Started ServerConnector@{HTTP/1.1,[http/1.1]";
        if (app.enableWebSocket()) {
            System.out.println(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
        }

        System.out.println(connectorInfo + "}{http://localhost:" + _port + "}");

        LogUtil.global().info("Server:main: tomcat: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            LogUtil.global().info("Server:main: tomcat: Has Stopped " + solon_boot_ver());
        }
    }
}
