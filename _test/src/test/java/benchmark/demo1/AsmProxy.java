package benchmark.demo1;


import org.noear.solon.extend.aspect.asm.Proxy;

import java.lang.reflect.Constructor;

public class AsmProxy {
    public static <T> T get(Class<T> clz, Object bean) throws NoSuchMethodException, SecurityException{
        Constructor constructor = clz.getConstructor(new Class[]{});
        Object[] constructorParam = new Object[]{};

        return (T) Proxy.newProxyInstance(
                AsmProxy.class.getClassLoader(),
                new BeanInvocation(bean),
                clz, constructor, constructorParam);
    }
}
