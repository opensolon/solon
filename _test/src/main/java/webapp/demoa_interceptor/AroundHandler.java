package webapp.demoa_interceptor;

import org.noear.solon.annotation.XAround;
import org.noear.solon.core.XContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AroundHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        XContext.current().output("@XAround:我也加一点；");
        return method.invoke(proxy, args);
    }
}
