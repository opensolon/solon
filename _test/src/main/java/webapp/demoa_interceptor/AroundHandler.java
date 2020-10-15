package webapp.demoa_interceptor;

import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodHandler;
import org.noear.solon.core.XContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AroundHandler implements MethodHandler {

    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] params, Object[] args, MethodChain invokeChain) throws Throwable {
        XContext.current().output("@XAround:我也加一点；");
        return invokeChain.doInvoke(obj, args);
    }
}
