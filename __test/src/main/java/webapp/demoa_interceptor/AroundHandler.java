package webapp.demoa_interceptor;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.aspect.Invocation;

public class AroundHandler implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Context.current().output("@Around:我也加一点；");
        return inv.invoke();
    }
}
