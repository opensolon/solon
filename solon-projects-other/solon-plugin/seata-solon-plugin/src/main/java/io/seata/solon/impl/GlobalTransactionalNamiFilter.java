package io.seata.solon.impl;


import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;

/**
 * Nami 过滤器（透递 seata 信息）
 *
 * @author noear
 * @since 2.5
 */
public class GlobalTransactionalNamiFilter implements Filter {
    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        return null;
    }
}
