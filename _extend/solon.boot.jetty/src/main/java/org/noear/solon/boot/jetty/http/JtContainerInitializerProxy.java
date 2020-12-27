package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.noear.solon.Utils;
import org.noear.solon.boot.servlet.SolonServletContainerInitializer;

import javax.servlet.*;
import java.util.*;

public class JtContainerInitializerProxy extends AbstractLifeCycle.AbstractLifeCycleListener {
    SolonServletContainerInitializer initializer;
    ServletContext sc;

    public JtContainerInitializerProxy(ServletContext servletContext) {
        this.sc = servletContext;
        this.initializer = new SolonServletContainerInitializer();
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            initializer.onStartup(new HashSet<Class<?>>(), sc);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
