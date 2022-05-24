package org.noear.solon.boot.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.noear.solon.Solon;
import org.noear.solon.boot.jetty.http.JtJspStarter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

class PluginJettyJsp extends PluginJetty {

    public PluginJettyJsp(int port, String host) {
        super(port, host);
    }

    /**
     * 获取Server Handler
     * */
    @Override
    protected Handler buildHandler() throws IOException{
        ServletContextHandler handler = getServletHandler();

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

        Properties properties = Solon.cfg().getProp("solon.jetty.jsp");
        properties.forEach((k, v) -> holderJsp.setInitParameter((String)k, (String)v));

        handler.addServlet(holderJsp, "*.jsp");
    }
}
