package org.noear.solon.boot.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.XPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

final class XPluginJettyJsp implements XPlugin {

    private Server _server = null;

    @Override
    public void start(XApp app) {

        XProperties props = app.prop();


        try {

            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");
            servletContextHandler.setBaseResource(new ResourceCollection(getResourceURLs()));
            servletContextHandler.addServlet(JspHttpContextServlet.class, "/");

            if(XServerProp.session_timeout >0) {
                servletContextHandler.getSessionHandler().setMaxInactiveInterval(XServerProp.session_timeout);
            }

            enableJspSupport(servletContextHandler);

            _server = new Server(app.port());
            _server.setSessionIdManager(new DefaultSessionIdManager(_server));
            _server.setHandler(servletContextHandler);

            _server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",
                    XServerProp.request_maxRequestSize);

            if (props != null) {
                props.forEach((k, v) -> {
                    String key = k.toString();
                    if (key.indexOf(".jetty.") > 0) {
                        _server.setAttribute(key, v);
                    }
                });

                props.onChange((k,v)->{
                    if (k.indexOf(".jetty.") > 0) {
                        _server.setAttribute(k, v);
                    }
                });
            }

            _server.start();
            app.onStop(this::stop);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop(){
        try {
            _server.stop();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void enableJspSupport(ServletContextHandler servletContextHandler) throws IOException {
        // Establish Scratch directory for the servlet context (used by JSP compilation)
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "solon.boot.jetty.jsp");

        if (!scratchDir.exists()) {
            if (!scratchDir.mkdirs()) {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        servletContextHandler.setAttribute("javax.servlet.context.tempdir", scratchDir);

        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        servletContextHandler.setClassLoader(jspClassLoader);

        // Manually call JettyJasperInitializer on context startup
        servletContextHandler.addBean(new JspStarter(servletContextHandler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);

        Properties properties = XApp.global().prop().getProp("solon.jetty.jsp");
        properties.forEach((k, v) -> holderJsp.setInitParameter((String)k, (String)v));

        servletContextHandler.addServlet(holderJsp, "*.jsp");
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
