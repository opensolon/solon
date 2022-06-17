package org.noear.solon.extend.servlet.holder;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import java.util.Objects;

@Deprecated
public class ServletHolder {
    public final WebServlet anno;
    public final Servlet servlet;

    public ServletHolder(WebServlet anno, Servlet servlet) {
        this.anno = anno;
        this.servlet = servlet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletHolder that = (ServletHolder) o;
        return Objects.equals(anno, that.anno) &&
                Objects.equals(servlet, that.servlet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anno, servlet);
    }
}
