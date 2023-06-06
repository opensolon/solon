package org.noear.solon.proxy.asm;


import org.noear.solon.core.AopContext;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AsmProxy {
    public static final int ASM_VERSION = Opcodes.ASM9;
    // 动态生成代理类的后缀
    public static final String PROXY_CLASSNAME_SUFFIX ="$$SolonAsmProxy";
    // 方法名
    private static final String METHOD_SETTER = "setInvocationHandler";


    public static Class<?> getProxyClass(AopContext context, Class<?> targetClass) throws Exception {
        String proxyClassName = targetClass.getName() + PROXY_CLASSNAME_SUFFIX;

        //目标代理类名
        Class<?> proxyClass = null;

        //确定代理类加载器
        AsmProxyClassLoader classLoader = (AsmProxyClassLoader) context.getAttrs().get(AsmProxyClassLoader.class);
        if (classLoader == null) {
            classLoader = new AsmProxyClassLoader(context.getClassLoader());
            context.getAttrs().put(AsmProxyClassLoader.class, classLoader);
        } else {
            //尝试获取类
            proxyClass = ClassUtil.loadClass(classLoader, proxyClassName);
        }

        if (proxyClass == null) {
            //构建新的代理类
            proxyClass = ClassCodeBuilder.build(targetClass, classLoader);
        }

        return proxyClass;
    }

    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @return 代理实例
     */
    public static Object newProxyInstance(AopContext context,
                                          InvocationHandler invocationHandler,
                                          Class<?> targetClass) {
        try {
            Constructor constructor = targetClass.getConstructor(new Class[]{});
            Object[] constructorParam = new Object[]{};

            return newProxyInstance(context, invocationHandler, targetClass, constructor, constructorParam);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to generate the proxy instance: " + targetClass.getName(), e);
        }
    }

    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @param targetConstructor 被代理对象的某一个构造器，用于决定代理对象实例化时采用哪一个构造器
     * @param targetParam       被代理对象的某一个构造器的参数，用于实例化构造器
     * @return 代理实例
     */
    public static Object newProxyInstance(AopContext context,
                                          InvocationHandler invocationHandler,
                                          Class<?> targetClass,
                                          Constructor<?> targetConstructor,
                                          Object... targetParam) {
        if (targetClass == null || invocationHandler == null) {
            throw new IllegalArgumentException("argument is null");
        }

        try {
            Class<?> proxyClass = getProxyClass(context, targetClass);

            // 实例化代理对象
            return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
        } catch (Exception e) {
            LogUtil.global().warn("can not proxy, targetClass: " + targetClass.getCanonicalName());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据被代理类的构造器，构造代理类对象。生成代理类的实例时调用其setter方法
     */
    private static Object newInstance(Class<?> proxyClass,
                                      InvocationHandler invocationHandler,
                                      Constructor<?> targetConstructor,
                                      Object... targetParam) throws Exception {
        Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
        Constructor<?> constructor = proxyClass.getConstructor(parameterTypes);
        Object instance = constructor.newInstance(targetParam);
        Method setterMethod = proxyClass.getDeclaredMethod(METHOD_SETTER, InvocationHandler.class);
        setterMethod.setAccessible(true);
        setterMethod.invoke(instance, invocationHandler);
        return instance;
    }
}