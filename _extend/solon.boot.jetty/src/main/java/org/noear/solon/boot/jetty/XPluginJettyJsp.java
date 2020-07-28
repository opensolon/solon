package org.noear.solon.boot.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

class XPluginJettyJsp extends XPluginJetty {

    /**
     * 获取Server Handler
     * */
    @Override
    protected Handler getServerHandler() throws IOException{
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        handler.setBaseResource(new ResourceCollection(getResourceURLs()));
        handler.addServlet(JtHttpContextHandlerJsp.class, "/");

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
        handler.addBean(new JspStarter(handler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);

        Properties properties = XApp.global().prop().getProp("solon.jetty.jsp");
        properties.forEach((k, v) -> holderJsp.setInitParameter((String)k, (String)v));

        handler.addServlet(holderJsp, "*.jsp");
    }

    private String[] getResourceURLs() throws FileNotFoundException {

        URL rootURL = getRootPath();
        if (rootURL == null) {
            throw new FileNotFoundException("Unable to find root");
        }
        String resURL = rootURL.toString();

        boolean isDebug = (XApp.global().prop().argx().getInt("debug") == 1);
        if (isDebug && (resURL.startsWith("jar:") == false)) {
            int endIndex = resURL.indexOf("target");
            String debugResURL = resURL.substring(0, endIndex) + "src/main/resources/";
            return new String[]{debugResURL, resURL};
        }

        return new String[]{resURL};
    }

    private URL getRootPath() {
        URL root = XUtil.getResource("/");
        if (root != null) {
            return root;
        }
        try {
            String path = XUtil.getResource("").toString();
            if (path.startsWith("jar:")) {
                int endIndex = path.indexOf("!");
                path = path.substring(0, endIndex + 1) + "/";
            } else {
                return null;
            }
            return new URL(path);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
