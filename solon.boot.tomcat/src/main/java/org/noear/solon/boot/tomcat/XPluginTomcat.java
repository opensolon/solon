package org.noear.solon.boot.tomcat;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.core.XPlugin;

import java.io.File;


/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:49
 * @Description : Yukai is so handsome xxD
 */
public class XPluginTomcat implements XPlugin {
    private static Tomcat tomcat;

    @Override
    public void start(XApp app) {
        Tomcat tomcat = getInstance();
        tomcat.setBaseDir("temp");
        tomcat.setPort(app.port());


        String contextPath = "/";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        String servletName = "actionServlet";
        tomcat.addServlet(contextPath, servletName, new TomcatServlet());
        context.addServletMappingDecoded(contextPath, servletName);
        //**************session time setting start*****************
        XProperties props = app.prop();
        int s_timeout = props.getInt("server.session.timeout", 0);
        if (s_timeout > 0) {
            context.setSessionTimeout(s_timeout);
        }
        //**************session time setting end*****************
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        app.onStop(this::stop);
        tomcat.getServer().await();
    }

    public void stop() {
        try {
            tomcat.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Tomcat getInstance() {
        synchronized (XPluginTomcat.class) {
            if (tomcat == null) {
                synchronized (XPluginTomcat.class) {
                    tomcat = new Tomcat();
                }
            }
        }
        return tomcat;
    }


}
