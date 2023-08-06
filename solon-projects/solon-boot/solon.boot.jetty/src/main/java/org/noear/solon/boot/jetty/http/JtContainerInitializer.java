package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.component.LifeCycle;
import org.noear.solon.web.servlet.SolonServletInstaller;

import javax.servlet.*;
import java.util.*;

public class JtContainerInitializer implements LifeCycle.Listener {
    SolonServletInstaller initializer;
    ServletContext sc;

    public JtContainerInitializer(ServletContext servletContext) {
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
