package webapp.demoa_interceptor;

import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.core.handle.Context;

public class AroundHandler implements Interceptor {

    @Override
    public Object doIntercept(Object obj, Object[] args, InterceptorChain chain) throws Throwable {
        Context.current().output("@Around:我也加一点；");
        return chain.doIntercept(obj, args);
    }
}
