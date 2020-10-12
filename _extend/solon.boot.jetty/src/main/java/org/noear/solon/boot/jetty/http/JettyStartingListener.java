package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.noear.solon.XUtil;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.HashSet;

public class JettyStartingListener extends AbstractLifeCycle.AbstractLifeCycleListener {
    ServletContext sc;
    ServletContainerInitializer sci;

    public JettyStartingListener(ServletContext servletContext, ServletContainerInitializer initializer) {
        sc = servletContext;
        sci = initializer;
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            sci.onStartup(new HashSet<Class<?>>(), sc);
        } catch (Exception ex) {
            throw XUtil.throwableWrap(ex);
        }
    }
}
