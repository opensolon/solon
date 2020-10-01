package org.noear.solon.boot.jetty.http;

import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class JspStarter extends AbstractLifeCycle
        implements ServletContextHandler.ServletContainerInitializerCaller {
    JettyJasperInitializer sci;
    ServletContextHandler context;

    public JspStarter(ServletContextHandler context) {

        this.sci = new JettyJasperInitializer();
        this.context = context;
        this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
    }

    @Override
    protected void doStart() throws Exception {

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            sci.onStartup(null, context.getServletContext());
            super.doStart();
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }
}