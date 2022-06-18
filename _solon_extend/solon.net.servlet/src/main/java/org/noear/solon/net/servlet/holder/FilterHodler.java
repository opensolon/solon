package org.noear.solon.net.servlet.holder;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import java.util.Objects;

public class FilterHodler {
    public final WebFilter anno;
    public final Filter filter;

    public FilterHodler(WebFilter anno, Filter filter) {
        this.anno = anno;
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterHodler that = (FilterHodler) o;
        return Objects.equals(anno, that.anno) &&
                Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anno, filter);
    }
}
