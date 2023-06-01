package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.*;

/**
 * @author noear 2023/5/31 created
 */
@Component
public class RouterInterceptorImpl2 implements RouterInterceptor, PathLimiter {

    @Override
    public PathRule pathRule() {
        return new PathRule().include("/admin/**").exclude("/admin/login");
    }

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        chain.doIntercept(ctx, mainHandler);
    }
}
