package org.noear.solon.boot.jetty;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.jetty.http.XFormContentFilter;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class XPluginImp implements Plugin {
    private XPluginJetty _server = null;

    public static String solon_boot_ver(){
        return "jetty 9.4/" + Solon.cfg().version();
    }


    @Override
    public void start(Solon app) {
        if (app.enableHttp() == false) {
            return;
        }

        Aop.context().beanBuilderAdd(WebFilter.class,(clz, bw, ano)->{});
        Aop.context().beanBuilderAdd(WebServlet.class,(clz, bw, ano)->{});
        Aop.context().beanBuilderAdd(WebListener.class,(clz, bw, ano)->{});

        XServerProp.init();

        Aop.beanOnloaded(() -> {
            start0(app);
        });
    }

    private void start0(Solon app) {
        Class<?> jspClz = Utils.loadClass("org.eclipse.jetty.jsp.JettyJspServlet");

        if (jspClz == null) {
            _server = new XPluginJetty();
        } else {
            _server = new XPluginJettyJsp();
        }

        long time_start = System.currentTimeMillis();
        System.out.println("solon.Server:main: Jetty 9.4(jetty)");

        _server.start(app);

        long time_end = System.currentTimeMillis();

        System.out.println("solon.Connector:main: jetty: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
        System.out.println("solon.Server:main: jetty: Started @" + (time_end - time_start) + "ms");


        app.before("**", new XFormContentFilter());
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            System.out.println("solon.Server:main: jetty: Has Stopped " + solon_boot_ver());
        }
    }
}
