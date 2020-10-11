package org.noear.solon.boot.jetty;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.boot.jetty.http.XFormContentFilter;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public final class XPluginImp implements XPlugin {
    private XPlugin _server = null;

    public static String solon_boot_ver(){
        return "jetty 9.4/" + XApp.cfg().version();
    }


    @Override
    public void start(XApp app) {
        if (app.enableHttp() == false) {
            return;
        }

        XServerProp.init();


        Class<?> jspClz = XUtil.loadClass("org.eclipse.jetty.jsp.JettyJspServlet");

        if (jspClz == null) {
            _server = new XPluginJetty(app);
        } else {
            _server = new XPluginJettyJsp(app);
        }

        Aop.beanOnloaded(()->{
            long time_start = System.currentTimeMillis();
            System.out.println("solon.Server:main: Jetty 9.4(jetty)");

            _server.start(app);

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: jetty: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: jetty: Started @" + (time_end - time_start) + "ms");
        });

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
