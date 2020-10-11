package org.noear.solon.servlet;

import javax.servlet.Filter;

public class FilterHolder {
    public final Filter filter;
    public final String paths[];

    public FilterHolder(Filter filter, String... paths) {
        this.filter = filter;
        this.paths = paths;
    }
}
