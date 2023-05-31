package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.core.route.RouterRuleInterceptor;

/**
 * @author noear 2023/5/31 created
 */
@Component
public class RouterRuleInterceptorImpl extends RouterRuleInterceptor {
    public RouterRuleInterceptorImpl() {
        //可以内部定制规则，也可以在外部定制
        exclude("/login");
    }

    @Override
    protected void doRuleIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        chain.doIntercept(ctx, mainHandler);
    }
}
