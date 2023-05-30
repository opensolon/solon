package org.noear.solon.core.util;

import org.noear.solon.core.JarClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * 类操作工具
 *
 * @author noear
 * @since 2.2
 */
public class ClassUtil {

    /**
     * 是否存在某个类
     *
     * @param test 检测函数
     */
    public static boolean hasClass(SupplierEx<Class<?>> test) {
        try {
            test.get();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 根据字符串加载为一个类
     *
     * @param className 类名称
     */
    public static Class<?> loadClass(String className) {
        return loadClass(null, className); //Class.forName(className);
    }

    /**
     * 根据字符串加载为一个类
     *
     * @param classLoader 类加载器
     * @param className   类名称
     */
    public static Class<?> loadClass(ClassLoader classLoader, String className) {
        try {
            if (classLoader == null) {
                return Class.forName(className);
            } else {
                return classLoader.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 尝试根据类名实例化一个对象
     *
     * @param className 类名称
     */
    public static <T> T tryInstance(String className) {
        return tryInstance(className, null);
    }


    /**
     * 尝试根据类名实例化一个对象
     *
     * @param className 类名称
     * @param prop      属性
     */
    public static <T> T tryInstance(String className, Properties prop) {
        return tryInstance(JarClassLoader.global(), className, prop);
    }


    /**
     * 尝试根据类名实例化一个对象
     *
     * @param classLoader 类加载器
     * @param className   类名称
     */
    public static <T> T tryInstance(ClassLoader classLoader, String className) {
        return tryInstance(classLoader, className, null);
    }


    /**
     * 尝试根据类名实例化一个对象
     *
     * @param classLoader 类加载器
     * @param className   类名称
     * @param prop        属性
     */
    public static <T> T tryInstance(ClassLoader classLoader, String className, Properties prop) {
        Class<?> clz = loadClass(classLoader, className);

        if (clz == null) {
            return null;
        } else {
            try {
                return newInstance(clz, prop);
            } catch (Throwable e) {
                return null; //如果异常，说明不支持这种实例化。跳过
            }
        }
    }

    /**
     * @deprecated 2.3
     */
    @Deprecated
    public static <T> T newInstance(String className) {
        return tryInstance(className);
    }

    /**
     * @deprecated 2.3
     */
    @Deprecated
    public static <T> T newInstance(String className, Properties prop) {
        return tryInstance(className, prop);
    }

    /**
     * @deprecated 2.3
     */
    @Deprecated
    public static <T> T newInstance(ClassLoader classLoader, String className) {
        return tryInstance(classLoader, className);
    }

    /**
     * @deprecated 2.3
     */
    @Deprecated
    public static <T> T newInstance(ClassLoader classLoader, String className, Properties prop) {
        return tryInstance(classLoader, className, prop);
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param clz 类
     */
    public static <T> T newInstance(Class<?> clz) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return newInstance(clz, null);
    }


    /**
     * 根据类名实例化一个对象
     *
     * @param clz  类
     * @param prop 属性
     */
    public static <T> T newInstance(Class<?> clz, Properties prop) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        if (prop == null) {
            return (T) clz.getDeclaredConstructor().newInstance();
        } else {
            return (T) clz.getConstructor(Properties.class).newInstance(prop);
        }
    }
}