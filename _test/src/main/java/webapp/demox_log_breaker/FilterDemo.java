package webapp.demox_log_breaker;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * @author noear 2021/3/8 created
 */
@Component
public class FilterDemo implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        System.out.println("我是好人过滤器!!!path=" + ctx.path() + ", pathNew=" + ctx.pathNew());
        chain.doFilter(ctx);
    }
}
