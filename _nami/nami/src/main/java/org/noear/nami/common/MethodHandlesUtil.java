package org.noear.nami.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * jdk8中如果直接调用{@link MethodHandles#lookup()}获取到的{@link MethodHandles.Lookup}
 * <p>
 * 在调用方法 {@link MethodHandles.Lookup#findSpecial(java.lang.Class, java.lang.String, java.lang.invoke.MethodType, java.lang.Class)}
 * <p>
 * 和{@link MethodHandles.Lookup#unreflectSpecial(java.lang.reflect.Method, java.lang.Class)}
 * 获取父类方法句柄{@link MethodHandle}时
 * <p>
 * 可能出现权限不够, 抛出如下异常, 所以通过反射创建{@link MethodHandles.Lookup}解决该问题.
 * <pre>
 *   java.lang.IllegalAccessException: no private access for invokespecial: interface com.example.demo.methodhandle.UserService, from com.example.demo.methodhandle.UserServiceInvoke
 * at java.lang.invoke.MemberName.makeAccessException(MemberName.java:850)
 * at java.lang.invoke.MethodHandles$Lookup.checkSpecialCaller(MethodHandles.java:1572)
 * </pre>
 * <p>
 * 而jdk11中直接调用{@link MethodHandles#lookup()}获取到的{@link MethodHandles.Lookup},也只能对接口类型才会权限获取方法的方法句柄{@link MethodHandle}.
 * <p>
 * 如果是普通类型Class,需要使用jdk9开始提供的 MethodHandles#privateLookupIn(java.lang.Class, java.lang.invoke.MethodHandles.Lookup)方法.
 */
public class MethodHandlesUtil {
    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;

    private static Constructor<MethodHandles.Lookup> java8LookupConstructor;
    private static Method privateLookupInMethod;

    static {
        //先查询jdk9 开始提供的java.lang.invoke.MethodHandles.privateLookupIn方法,
        //如果没有说明是jdk8的版本.(不考虑jdk8以下版本)
        try {
            privateLookupInMethod = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupInMethod = null;
            System.out.println("There is no [java.lang.invoke.MethodHandles.privateLookupIn(Class, Lookup)] method in this version of JDK");
        }
        //jdk8
        //这种方式其实也适用于jdk9及以上的版本,但是上面优先,可以避免 jdk9 反射警告
        if (privateLookupInMethod == null) {
            try {
                java8LookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                java8LookupConstructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                //可能是jdk8 以下版本
                throw new IllegalStateException(
                        "There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
                        e);
            }
        }
    }

    /**
     * java9中的MethodHandles.lookup()方法返回的Lookup对象
     * 有权限访问specialCaller != lookupClass()的类
     * 但是只能适用于接口, {@link java.lang.invoke.MethodHandles.Lookup#checkSpecialCaller}
     */
    public static MethodHandles.Lookup lookup(Class<?> callerClass) {
        //使用反射,因为当前jdk可能不是java9或以上版本
        if (privateLookupInMethod != null) {
            try {
                return (MethodHandles.Lookup) privateLookupInMethod.invoke(MethodHandles.class, callerClass, MethodHandles.lookup());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        //jdk 8
        try {
            return java8LookupConstructor.newInstance(callerClass, ALLOWED_MODES);
        } catch (Exception e) {
            throw new IllegalStateException("no 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.", e);
        }
    }

    public static MethodHandle getSpecialMethodHandle(Method parentMethod) {
        final Class<?> declaringClass = parentMethod.getDeclaringClass();
        MethodHandles.Lookup lookup = lookup(declaringClass);
        try {
            return lookup.unreflectSpecial(parentMethod, declaringClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
