package webapp.demoa_interceptor;

import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.core.XContext;

public class AroundHandler implements MethodInterceptor {

    @Override
    public Object doIntercept(Object obj, MethodHolder mH, Object[] args, MethodChain invokeChain) throws Throwable {
        XContext.current().output("@XAround:我也加一点；");
        return invokeChain.doInvoke(obj, args);
    }
}
