package org.noear.solon.aspect.apt;

import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

/**
 * @author noear
 * @since 2.1
 */
public class AptProxy {
    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass（或许是算静态代理类）
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @return 代理实例
     */
    public static Object newProxyInstance(AopContext context,
                                          InvocationHandler invocationHandler,
                                          Class<?> targetClass) {
        //支持APT (支持 Graalvm Native  打包)
        String proxyClassName = targetClass.getName() + "$$SolonProxy";
        Class<?> proxyClass = Utils.loadClass(context.getClassLoader(), proxyClassName);

        if (proxyClass == null) {
            return null;
        } else {
            try {
                Constructor constructor = proxyClass.getConstructor(InvocationHandler.class);
                return constructor.newInstance(invocationHandler);
            }catch (Exception e){
                throw new IllegalStateException("Failed to generate the proxy instance: " + targetClass.getName(), e);
            }
        }
    }
}
