package org.noear.solon.core.handle;

import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * 过滤器调用链实现
 *
 * @author noear
 * @since 1.3
 * */
public class FilterChainImpl implements FilterChain {
    private final List<RankEntity<Filter>> filterList;
    private int index;

    public FilterChainImpl(List<RankEntity<Filter>> filterList) {
        this.filterList = filterList;
        this.index = 0;
    }

    @Override
    public void doFilter(Context ctx) throws Throwable {
        filterList.get(index++).target.doFilter(ctx, this);
    }
}
