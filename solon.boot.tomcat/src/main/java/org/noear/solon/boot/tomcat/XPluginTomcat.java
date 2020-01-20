package org.noear.solon.boot.tomcat;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.XApp;
import org.noear.solon.XAppProperties;
import org.noear.solon.core.XPlugin;


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
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));
        tomcat.setPort(app.port());
        //context configuration start 开始上下文相关配置
        Context context = stepContext(tomcat,app);

        //**************session time setting start Session时间相关*****************
        XAppProperties props = app.prop();
        int s_timeout = props.getInt("server.session.timeout", 0);
        if (s_timeout > 0) {
            context.setSessionTimeout(s_timeout);
        }
        //**************session time setting end*****************
        context.setAllowCasualMultipartParsing(true);
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        //Tomcat运行需要进入阻塞阶段，这里新开一个线程维护
        new Thread(() -> tomcat.getServer().await()).start();
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
    protected Context stepContext(Tomcat tomcat, XApp app) {
        String contextPath = "/";
        //  String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, null);//第二个参数与文档相关
        String servletName = "actionServlet";
        tomcat.addServlet(contextPath, servletName, new TCHttpContextHandler(app));
        context.addServletMappingDecoded(contextPath, servletName);//Servlet与对应uri映射
        return context;
    }

    @Override
    public void stop() throws Throwable {
        if (tomcat != null) {
            tomcat.stop();
            tomcat = null;
        }
    }
}
