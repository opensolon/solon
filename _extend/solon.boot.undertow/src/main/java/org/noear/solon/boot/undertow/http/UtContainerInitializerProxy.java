package org.noear.solon.boot.undertow.http;

import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.noear.solon.boot.undertow.holder.FilterHodler;
import org.noear.solon.boot.undertow.holder.ServletHolder;
import org.noear.solon.core.Aop;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import java.util.*;

public class UtContainerInitializerProxy implements ServletContainerInitializer {
    Set<ServletContainerInitializer> initializers = new LinkedHashSet<>();
    Set<FilterHodler> filters = new LinkedHashSet<>();
    Set<EventListener> listeners = new LinkedHashSet<>();
    Set<ServletHolder> servlets = new LinkedHashSet<>();

    public UtContainerInitializerProxy() {
        Aop.beanForeach((bw) -> {
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

    public static ServletContainerInitializerInfo info() {
        return new ServletContainerInitializerInfo(UtContainerInitializerProxy.class, null);
    }
}
