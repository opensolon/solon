/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.servlet;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.web.servlet.holder.FilterHodler;
import org.noear.solon.web.servlet.holder.ServletHolder;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;

import java.util.*;

/**
 * @author noear
 * @since 1.2
 */
public class SolonServletInstaller {
    Set<ServletContainerInitializer> initializers = new LinkedHashSet<>();
    Set<FilterHodler> filters = new LinkedHashSet<>();
    Set<EventListener> listeners = new LinkedHashSet<>();
    Set<ServletHolder> servlets = new LinkedHashSet<>();

    public SolonServletInstaller() {
        Solon.context().beanForeach((bw) -> {
            if (bw.raw() instanceof ServletContainerInitializer) {
                initializers.add(bw.raw());
            }

            if (bw.raw() instanceof EventListener) {
                WebListener anno = bw.clz().getAnnotation(WebListener.class);
                if (anno != null) {
                    listeners.add(bw.raw());
                }
            }

            if (bw.raw() instanceof Filter) {
                WebFilter anno = bw.clz().getAnnotation(WebFilter.class);
                if (anno != null) {
                    filters.add(new FilterHodler(anno, bw.raw()));
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

    public void startup(Set<Class<?>> set, ServletContext sc) throws ServletException {
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
            if (Utils.isEmpty(name)) {
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
            if (Utils.isEmpty(name)) {
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
}
