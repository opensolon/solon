package org.noear.solon.servlet;

import javax.servlet.Filter;

public class FilterHolder {
    private final Filter filter;
    private final String paths[];

    public FilterHolder(Filter filter, String... paths) {
        this.filter = filter;
        this.paths = paths;
    }

    public Filter getFilter() {
        return filter;
    }

    public String[] getPaths() {
        return paths;
    }
}
