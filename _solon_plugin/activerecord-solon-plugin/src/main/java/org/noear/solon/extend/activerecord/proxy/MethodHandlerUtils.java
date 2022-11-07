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
    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
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
     * 若jdk版本在1.8以上，且被调用的方法是默认方法，直接调用默认实现
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    public static Object invokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
        // https://dzone.com/articles/correct-reflective-access-to-interface-default-methods
        // https://gist.github.com/lukaseder/f47f5a0d156bf7b80b67da9d14422d4a
        if (JavaUtil.JAVA_MAJOR_VERSION <= 15) {
            //调用 default 和 object.class 的函数
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);

            final Class<?> clazz = method.getDeclaringClass();
            return constructor.newInstance(clazz, ALLOWED_MODES)
                    .in(clazz)
                    .unreflectSpecial(method, clazz)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } else {
            if (method.getDeclaringClass().equals(Object.class)) {
                //调用 object.class 的函数
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
    }
}
