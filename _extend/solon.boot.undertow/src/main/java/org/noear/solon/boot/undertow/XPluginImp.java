package org.noear.solon.boot.undertow;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

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

    private void start0(SolonApp app){
        long time_start = System.currentTimeMillis();
        System.out.println("solon.Server:main: Undertow 2.1.09(undertow)");

        Class<?> jspClz = Utils.loadClass("io.undertow.jsp.JspServletBuilder");

        if (jspClz == null) {
            _server = new PluginUndertow();
        }else{
            _server = new PluginUndertowJsp();
        }

        _server.start(app);

        long time_end = System.currentTimeMillis();

        System.out.println("solon.Connector:main: undertow: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
        System.out.println("solon.Server:main: undertow: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            System.out.println("solon.Server:main: undertow: Has Stopped " + solon_boot_ver());
        }
    }
}
