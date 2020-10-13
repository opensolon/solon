package org.noear.solon.boot.undertow.http;

import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.noear.solon.XUtil;
import org.noear.solon.boot.undertow.holder.FilterHodler;
import org.noear.solon.boot.undertow.holder.ServletHolder;
import org.noear.solon.core.Aop;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
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
            String[] urlPatterns = f.anno.value();
            if (urlPatterns.length == 0) {
                urlPatterns = f.anno.urlPatterns();
            }


            String name = f.anno.filterName();
            if (XUtil.isEmpty(name)) {
                name = f.filter.getClass().getSimpleName();
            }


            EnumSet<DispatcherType> enumSet = EnumSet.copyOf(Arrays.asList(f.anno.dispatcherTypes()));

            FilterRegistration.Dynamic dy = sc.addFilter(name, f.filter);

            for (WebInitParam ip : f.anno.initParams()) {
                dy.setInitParameter(ip.name(), ip.value());
            }


            if (urlPatterns.length > 0) {
                dy.addMappingForUrlPatterns(enumSet, false, urlPatterns);
            }

            if (f.anno.servletNames().length > 0) {
                dy.addMappingForServletNames(enumSet, false, f.anno.servletNames());
            }
        }

        for (ServletHolder s : servlets) {
            String[] urlPatterns = s.anno.value();
            if (urlPatterns.length == 0) {
                urlPatterns = s.anno.urlPatterns();
            }

            String name = s.anno.name();
            if (XUtil.isEmpty(name)) {
                name = s.servlet.getClass().getSimpleName();
            }

            ServletRegistration.Dynamic dy = sc.addServlet(name, s.servlet);

            for (WebInitParam ip : s.anno.initParams()) {
                dy.setInitParameter(ip.name(), ip.value());
            }

            dy.addMapping(urlPatterns);
            dy.setLoadOnStartup(s.anno.loadOnStartup());

        }
    }

    public static ServletContainerInitializerInfo info() {
        return new ServletContainerInitializerInfo(UtContainerInitializerProxy.class, null);
    }
}
