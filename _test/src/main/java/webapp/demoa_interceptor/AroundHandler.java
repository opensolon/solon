package webapp.demoa_interceptor;

import org.noear.solon.core.InvokeChain;
import org.noear.solon.core.InvokeHandler;
import org.noear.solon.core.XContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AroundHandler implements InvokeHandler {

    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] params, Object[] args, InvokeChain invokeChain) throws Throwable {
        XContext.current().output("@XAround:我也加一点；");
        return invokeChain.doInvoke(obj, args);
    }
}
