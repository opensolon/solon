package org.noear.solon.boot.jetty.holder;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;

public class FilterHodler {
    public final WebFilter anno;
    public final Filter filter;

    public FilterHodler(WebFilter anno, Filter filter) {
        this.anno = anno;
        this.filter = filter;
    }
}
