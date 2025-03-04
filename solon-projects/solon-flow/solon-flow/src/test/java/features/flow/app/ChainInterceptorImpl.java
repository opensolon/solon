package features.flow.app;

import org.noear.solon.annotation.Component;
import org.noear.solon.flow.intercept.ChainInterceptor;
import org.noear.solon.flow.intercept.ChainInvocation;

/**
 * @author noear 2025/3/4 created
 */
@Component
public class ChainInterceptorImpl implements ChainInterceptor {
    @Override
    public void doIntercept(ChainInvocation invocation) throws Throwable {
        System.out.println("doIntercept---------------");
        invocation.invoke();
    }
}
