package benchmark;

import java.lang.reflect.Proxy;

public class BeanProxy {
    public static <T> T get(Class<T> clz, Object bean) {
        return (T) Proxy.newProxyInstance(
                BeanProxy.class.getClassLoader(),
                new Class[]{clz},
                new BeanInvocation(bean));
    }
}
