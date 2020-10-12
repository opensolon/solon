package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.noear.solon.XUtil;
import org.noear.solon.boot.jetty.holder.FilterHodler;
import org.noear.solon.boot.jetty.holder.ServletHolder;
import org.noear.solon.core.Aop;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import java.util.*;

public class JtContainerInitializerProxy extends AbstractLifeCycle.AbstractLifeCycleListener {
    ServletContext sc;
    List<ServletContainerInitializer> initializers = new ArrayList<>();
    List<FilterHodler> filters = new ArrayList<>();
    List<EventListener> listeners = new ArrayList<>();
    List<ServletHolder> servlets = new ArrayList<>();

    public JtContainerInitializerProxy(ServletContext servletContext) {
        sc = servletContext;

        Aop.beanForeach((k, bw) -> {
            if (bw.raw() instanceof ServletContainerInitializer) {
                initializers.add(bw.raw());
            }

            if (bw.raw() instanceof Filter) {
                WebFilter anno = bw.clz().getAnnotation(WebFilter.class);
                if (anno != null) {
                    filters.add(new FilterHodler(anno, bw.raw()));
                }
            }

            if (bw.raw() instanceof EventListener) {
                WebListener anno = bw.clz().getAnnotation(WebListener.class);
                if (anno != null) {
                    listeners.add(bw.raw());
                }
            }

            if (bw.raw() instanceof Servlet) {
                WebServlet anno = bw.clz().getAnnotation(WebServlet.class);
                if (anno != null) {
                    servlets.add(new ServletHolder(anno, bw.raw()));
                }
            }
        });
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            onStartup(new HashSet<Class<?>>(), sc);
        } catch (Exception ex) {
            throw XUtil.throwableWrap(ex);
        }
    }

    public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
        for (ServletContainerInitializer si : initializers) {
            si.onStartup(set, sc);
        }

        for (EventListener l : listeners) {
            sc.addListener(l);
        }

        for (FilterHodler f : filters) {
            FilterRegistration.Dynamic dy = sc.addFilter(f.anno.filterName(), f.filter);
            dy.addMappingForUrlPatterns(
                    EnumSet.copyOf(Arrays.asList(f.anno.dispatcherTypes())),
                    false,
                    f.anno.urlPatterns()
            );
        }

        for (ServletHolder s : servlets) {
            ServletRegistration.Dynamic dy = sc.addServlet(s.anno.name(), s.servlet);
            dy.setLoadOnStartup(s.anno.loadOnStartup());
            dy.addMapping(s.anno.urlPatterns());
        }
    }
}
