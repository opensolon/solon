package org.noear.solon.boot.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.noear.solon.boot.jetty.http.JtJspStarter;
import org.noear.solon.boot.jetty.jsp.JspTldLocator;

import javax.servlet.ServletContext;
import javax.servlet.descriptor.TaglibDescriptor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

class JettyServerAddJsp extends JettyServer {

    /**
     * 获取Server Handler
     */
    @Override
    protected Handler buildHandler() throws IOException {
        ServletContextHandler handler = getServletHandler();

        enableJspSupport(handler);
        addTdlSupport(handler.getServletContext());

        return handler;
    }

    private void enableJspSupport(ServletContextHandler handler) throws IOException {
        // Establish Scratch directory for the servlet context (used by JSP compilation)
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "solon.boot.jetty");

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

        handler.addServlet(holderJsp, "*.jsp");
    }

    private void addTdlSupport(ServletContext servletContext) throws IOException {
        Map<String, TaglibDescriptor> tagLibInfos = JspTldLocator.createTldInfos("templates");

        if (tagLibInfos.size() > 0) {
            ServletContextHandler.JspConfig jspConfig = (ServletContextHandler.JspConfig) servletContext.getJspConfigDescriptor();
            if (jspConfig == null) {
                jspConfig = new ServletContextHandler.JspConfig();
                ((ServletContextHandler.Context) servletContext).setJspConfigDescriptor(jspConfig);
            }

            for (TaglibDescriptor descriptor : tagLibInfos.values()) {
                jspConfig.addTaglibDescriptor(descriptor);
            }
        }
    }
}
