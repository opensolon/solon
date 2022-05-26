package org.noear.solon.boot.undertow.http;

import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.noear.solon.extend.servlet.SolonServletInstaller;

import javax.servlet.*;
import java.util.*;

public class UtContainerInitializerProxy implements ServletContainerInitializer {
    SolonServletInstaller initializer;

    public UtContainerInitializerProxy() {
        initializer = new SolonServletInstaller();
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
        initializer.startup(set, sc);
    }

    public static ServletContainerInitializerInfo info() {
        return new ServletContainerInitializerInfo(UtContainerInitializerProxy.class, null);
    }
}
