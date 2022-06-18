package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.noear.solon.net.servlet.SolonServletInstaller;

import javax.servlet.*;
import java.util.*;

public class JtContainerInitializerProxy extends AbstractLifeCycle.AbstractLifeCycleListener {
    SolonServletInstaller initializer;
    ServletContext sc;

    public JtContainerInitializerProxy(ServletContext servletContext) {
        this.sc = servletContext;
        this.initializer = new SolonServletInstaller();
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            initializer.startup(new HashSet<Class<?>>(), sc);
        } catch (ServletException ex) {
            throw new RuntimeException(ex);
        }
    }
}
