package org.noear.solon.core.handle;

import org.noear.solon.core.route.PathRule;

/**
 * 过滤器-限制器
 *
 * @author noear
 * @since 2.3
 */
public class FilterLimiter implements Filter {
    protected final PathRule rule;
    private final Filter filter;

    public FilterLimiter(Filter filter, PathRule rule) {
        this.rule = rule;
        this.filter = filter;
    }

    /**
     * 是否匹配
     */
    protected boolean isMatched(Context ctx) {
        return rule == null || rule.isEmpty() || rule.test(ctx.path());
    }

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (isMatched(ctx)) {
            //执行过滤
            filter.doFilter(ctx, chain);
        } else {
            //原路传递
            chain.doFilter(ctx);
        }
    }
}