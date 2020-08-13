package benchmark.demo1;

import java.lang.reflect.Proxy;

public class JdkProxy {
    public static <T> T get(Class<T> clz, Object bean) {
        return (T) Proxy.newProxyInstance(
                JdkProxy.class.getClassLoader(),
                new Class[]{clz},
                new BeanInvocation(bean));
    }
}
