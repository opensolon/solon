package org.noear.solon.core.handle;

/**
 * 过滤器调用链实现
 *
 * @author noear
 * @since 1.3
 * */
public class FilterChainNode implements FilterChain {
    static final FilterChainNode EMPTY = new FilterChainNodeEmpty();

    public Filter filter;
    public FilterChainNode next;

    private FilterChainNode() {

    }

    public FilterChainNode(Filter filter) {
        this.filter = filter;
        //将下一节点默认置为空
        this.next = EMPTY;
    }

    @Override
    public void doFilter(Context ctx) throws Throwable {
        filter.doFilter(ctx, next);
    }

    /**
     * 定义个空节点
     * */
    static class FilterChainNodeEmpty extends FilterChainNode {
        @Override
        public void doFilter(Context ctx) throws Throwable {
            //清空过滤处理
        }
    }
}
