package org.noear.solon.boot.jetty.holder;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;

public class ServletHolder {
    public final WebServlet anno;
    public final Servlet servlet;

    public ServletHolder(WebServlet anno, Servlet servlet) {
        this.anno = anno;
        this.servlet = servlet;
    }
}
