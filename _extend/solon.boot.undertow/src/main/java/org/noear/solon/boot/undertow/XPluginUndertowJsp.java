package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.DefaultClassIntrospector;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:50
 * @Description : Yukai is so handsome xxD
 */
public class XPluginUndertowJsp implements XPlugin {
    private static Undertow.Builder serverBuilder = null;
    private static Undertow _server = null;


    @Override
    public void start(XApp app) {
        try {
            setupJsp(app);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        _server.start();

    }


    public void setupJsp(XApp app) throws ServletException {
        final String KEY = "io.message";

        final ServletContainer container = ServletContainer.Factory.newInstance();
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], app.getClass().getClassLoader());

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(XPluginUndertowJsp.class.getClassLoader())
                .setDeploymentName("solon")
                .setContextPath("/")
                .setDefaultEncoding(XServerProp.encoding_request)
                .setClassIntrospecter(DefaultClassIntrospector.INSTANCE)
                .setResourceManager(new ClassPathResourceManager(jspClassLoader))
                .setDefaultMultipartConfig(new MultipartConfigElement(System.getProperty("java.io.tmpdir")))
                .addServlet(JspServletBuilder.createServlet("JSPServlet", "*.jsp"))
                .addServlet(new ServletInfo("ACTServlet", UnderServlet.class).addMapping("/"));  //这个才是根据上下文对象`XContext`进行分发

        if (XServerProp.session_timeout > 0) {
            builder.setDefaultSessionTimeout(XServerProp.session_timeout);
        }


        JspServletBuilder.setupDeployment(builder, new HashMap<String, JspPropertyGroup>(), new HashMap<String, TagLibraryInfo>(), new HackInstanceManager());

        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        HttpHandler jsp_handler = manager.start();

        //************************** init server start******************
        serverBuilder = getInstance().setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE,false);;
        serverBuilder.addHttpListener(app.port(), "0.0.0.0");
        serverBuilder.setHandler(jsp_handler);

        _server = serverBuilder.build();

        //************************* init server end********************
        System.setProperty(KEY, "Hello JSP!");
    }


    // 允许在其他代码层访问容器构造器实例
    public static Undertow.Builder getInstance() {
        synchronized (XPluginImp.class) {
            if (serverBuilder == null) {
                synchronized (XPlugin.class) {
                    serverBuilder = Undertow.builder();
                }
            }
        }
        return serverBuilder;
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }
}
