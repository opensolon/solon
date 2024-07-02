package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;

/**
 * @author noear 2023/1/10 created
 */
@Component(index = 1)
public class RouterInterceptorImpl implements RouterInterceptor {

    @Override
    public void doIntercept(Context ctx, @Nullable Handler handler, RouterInterceptorChain chain) throws Throwable {
        System.out.println("RouterInterceptor: xxxx");
        chain.doIntercept(ctx, handler);
    }

    @Override
    public void postArguments(Context ctx, ParamWrap[] args, Object[] vals) throws Throwable {
        if ("/demo2/param5/test5".equals(ctx.pathNew())) {
            vals[0] = "postArguments";
        }
    }
}
