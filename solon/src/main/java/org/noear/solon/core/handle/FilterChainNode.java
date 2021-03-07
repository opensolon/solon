package org.noear.solon.core.handle;

/**
 * 过滤器调用链实现
 *
 * @author noear
 * @since 1.3
 * */
public class FilterChainNode implements FilterChain {
    static final FilterChain nextDef = (c) -> {
    };

    private final Filter filter;
    public FilterChain next;

    public FilterChainNode(Filter filter) {
        this.filter = filter;
        this.next = nextDef;
    }

    @Override
    public void doFilter(Context ctx) throws Throwable {
        filter.doFilter(ctx, next);
    }
}
