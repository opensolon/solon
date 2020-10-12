package org.noear.solon.boot.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.noear.solon.XApp;
import org.noear.solon.boot.jetty.http.JtStartingListener;
import org.noear.solon.boot.jetty.http.JtJspStarter;
import org.noear.solon.boot.jetty.http.JtHttpContextServlet;
import org.noear.solon.core.Aop;

import javax.servlet.ServletContainerInitializer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

class XPluginJettyJsp extends XPluginJetty {

    /**
     * 获取Server Handler
     * */
    @Override
    protected Handler getServerHandler() throws IOException{
        ServletContextHandler handler = new ServletContextHandler();
        handler.setSessionHandler(new SessionHandler());
        handler.setContextPath("/");
//        handler.setDescriptor("/WEB-INF/web.xml");
        handler.addServlet(JtHttpContextServlet.class, "/");
        handler.setBaseResource(new ResourceCollection(getResourceURLs()));

        //尝试添加容器初始器
        ServletContainerInitializer initializer = Aop.getOrNull(ServletContainerInitializer.class);
        if (initializer != null) {
            handler.addLifeCycleListener(new JtStartingListener(handler.getServletContext(), initializer));
        }

        if (XServerProp.session_timeout > 0) {
            handler.getSessionHandler().setMaxInactiveInterval(XServerProp.session_timeout);
        }

        enableJspSupport(handler);

        return handler;
    }

    private void enableJspSupport(ServletContextHandler handler) throws IOException {
        // Establish Scratch directory for the servlet context (used by JSP compilation)
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "solon.boot.jetty.jsp");

        if (!scratchDir.exists()) {
            if (!scratchDir.mkdirs()) {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        handler.setAttribute("javax.servlet.context.tempdir", scratchDir);

        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        handler.setClassLoader(jspClassLoader);

        // Manually call JettyJasperInitializer on context startup
        handler.addBean(new JtJspStarter(handler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);

        Properties properties = XApp.global().prop().getProp("solon.jetty.jsp");
        properties.forEach((k, v) -> holderJsp.setInitParameter((String)k, (String)v));

        handler.addServlet(holderJsp, "*.jsp");
    }

}
