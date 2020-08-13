package benchmark;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BeanInvocation implements InvocationHandler {
    private Object bean;

    public BeanInvocation(Object bean) {
        this.bean = bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(bean, args);
    }
}
