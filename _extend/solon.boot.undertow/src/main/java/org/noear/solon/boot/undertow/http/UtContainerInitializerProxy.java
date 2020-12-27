package org.noear.solon.boot.undertow.http;

import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.noear.solon.boot.servlet.SolonServletContainerInitializer;

import javax.servlet.*;
import java.util.*;

public class UtContainerInitializerProxy implements ServletContainerInitializer {
    SolonServletContainerInitializer initializer;

    public UtContainerInitializerProxy() {
        initializer = new SolonServletContainerInitializer();
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
        initializer.onStartup(set, sc);
    }

    public static ServletContainerInitializerInfo info() {
        return new ServletContainerInitializerInfo(UtContainerInitializerProxy.class, null);
    }
}
