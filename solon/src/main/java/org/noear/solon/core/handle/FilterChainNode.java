package org.noear.solon.core.handle;

import java.util.List;

/**
 * 过滤器调用链实现
 *
 * @author noear
 * @since 1.3
 * */
public class FilterChainNode implements FilterChain {
    private final List<FilterEntity> filterList;
    private int index;

    public FilterChainNode(List<FilterEntity> filterList) {
        this.filterList = filterList;
        this.index = 0;
    }

    @Override
    public void doFilter(Context ctx) throws Throwable {
        filterList.get(index++).filter.doFilter(ctx, this);
    }
}
