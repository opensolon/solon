package webapp.demoa_interceptor;

import org.noear.solon.core.InterceptorChain;
import org.noear.solon.core.Interceptor;
import org.noear.solon.core.handler.Context;

public class AroundHandler implements Interceptor {

    @Override
    public Object doIntercept(Object obj, Object[] args, InterceptorChain chain) throws Throwable {
        Context.current().output("@XAround:我也加一点；");
        return chain.doIntercept(obj, args);
    }
}
