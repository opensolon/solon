package org.noear.solon.core.util;

import org.noear.solon.core.JarClassLoader;

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
     * 根据类名实例化一个对象
     *
     * @param className 类名称
     */
    public static <T> T newInstance(String className) {
        return newInstance(className, null);
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param className 类名称
     * @param prop      属性
     */
    public static <T> T newInstance(String className, Properties prop) {
        return newInstance(JarClassLoader.global(), className, prop);
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param classLoader 类加载器
     * @param className   类名称
     */
    public static <T> T newInstance(ClassLoader classLoader, String className) {
        return newInstance(classLoader, className, null);
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param classLoader 类加载器
     * @param className   类名称
     * @param prop        属性
     */
    public static <T> T newInstance(ClassLoader classLoader, String className, Properties prop) {
        try {
            Class<?> clz = loadClass(classLoader, className);
            if (clz == null) {
                return null;
            } else {
                return newInstance(clz, prop);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param clz 类
     */
    public static <T> T newInstance(Class<?> clz) throws Exception {
        return newInstance(clz, null);
    }


    /**
     * 根据类名实例化一个对象
     *
     * @param clz  类
     * @param prop 属性
     */
    public static <T> T newInstance(Class<?> clz, Properties prop) throws Exception {
        if (prop == null) {
            return (T) clz.getDeclaredConstructor().newInstance();
        } else {
            return (T) clz.getConstructor(Properties.class).newInstance(prop);
        }
    }
}