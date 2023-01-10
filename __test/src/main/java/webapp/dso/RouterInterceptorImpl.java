package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

/**
 * @author noear 2023/1/10 created
 */
@Component(index = 1)
public class RouterInterceptorImpl implements RouterInterceptor {

    @Override
    public void doIntercept(Context ctx, RouterInterceptorChain chain) throws Throwable {
        System.out.println("RouterInterceptor: xxxx");
        chain.doIntercept(ctx);
    }
}
