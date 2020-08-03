package org.noear.solon.boot.jetty;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

public final class XPluginImp implements XPlugin {
    private XPlugin _server = null;

    public static String solon_boot_ver(){
        return "jetty 9.4/1.0.9";
    }


    @Override
    public void start(XApp app) {
        if(app.enableHttp == false){
            return;
        }

        XServerProp.init();

        long time_start = System.currentTimeMillis();
        System.out.println("solon.Server:main: Jetty 9.4");

        Class<?> jspClz = XUtil.loadClass("org.eclipse.jetty.jsp.JettyJspServlet");

        if (jspClz == null) {
            _server = new XPluginJetty();
        } else {
            _server = new XPluginJettyJsp();
        }

        _server.start(app);

        long time_end = System.currentTimeMillis();

        System.out.println("solon.Connector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
        System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
        }
    }
}
