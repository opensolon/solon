package org.noear.solon.extend.activerecord.proxy;

import org.noear.solon.core.util.JavaUtil;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.10
 */
public class MethodHandlerUtils {
    /**
     * java16+ 支持调用default method的方法
     */
    private static Method invokeDefaultMethod = null;

    static {
        //
        //JDK16+ 新增InvocationHandler.invokeDefault()
        //
        if (JavaUtil.JAVA_MAJOR_VERSION >= 16) {
            // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8253870
            Method[] ms = InvocationHandler.class.getMethods();

            for (Method call : ms) {
                if ("invokeDefault".equals(call.getName())) {
                    invokeDefaultMethod = call;
                    break;
                }
            }
            if (invokeDefaultMethod == null) {
                //不可能发生
                throw new UnsupportedOperationException("The current java " + JavaUtil.JAVA_MAJOR_VERSION + " is not found: invokeDefault");
            }
        }
    }

    /**
     * 在代理模式下调用接口的默认的函数
     */
    public static Object invokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
        // https://dzone.com/articles/correct-reflective-access-to-interface-default-methods
        // https://gist.github.com/lukaseder/f47f5a0d156bf7b80b67da9d14422d4a
        if (JavaUtil.JAVA_MAJOR_VERSION <= 15) {
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);

            final Class<?> clazz = method.getDeclaringClass();
            return constructor.newInstance(clazz)
                    .in(clazz)
                    .unreflectSpecial(method, clazz)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } else {
            Method invoke = invokeDefaultMethod;
            return invoke.invoke(null, proxy, method, args);
        }
    }

    /**
     * 在代理模式下调用 Object 的默认的函数
     */
    public static Object invokeObject(Class<?> interfaceClz, Object proxy, Method method, Object[] args) {
        String name = method.getName();

        switch (name) {
            case "toString":
                return interfaceClz.getName() + ".$Proxy";
            case "hashCode":
                return System.identityHashCode(proxy);
            case "equals":
                return proxy == args[0];
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + interfaceClz.getName() + "::" + method.getName());
        }
    }
}
