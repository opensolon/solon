package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * @author noear 2021/11/13 created
 */
@Component(index = 1)
public class AppFilterImpl implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        chain.doFilter(ctx);
    }
}
