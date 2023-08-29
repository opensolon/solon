package io.seata.solon.integration;


import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;

/**
 * Nami 过滤器（透递 seata 信息）
 *
 * @author noear
 * @since 2.4
 */
public class NamiFilter implements Filter {
    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        return null;
    }
}
