package org.noear.solon.core.handle;

/**
 * @author noear
 * @since 1.4
 */
public class FilterEntity {
    public final int index;
    public final Filter filter;

    public FilterEntity(int index, Filter filter) {
        this.index = index;
        this.filter = filter;
    }
}
