package org.noear.solon.core.util;

import org.noear.solon.core.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 * @author songyinyin
 * @since 2.2
 */
public class ReflectUtil {

    public static final String CONSTRUCTOR_NAME = "<init>";

    static Reflection reflection;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        Reflection ext = ClassUtil.tryInstance("org.noear.solon.extend.impl.ReflectionExt");

        if (ext == null) {
            reflection = new Reflection();
        } else {
            reflection = ext;
        }
    }

    /**
     * 获取类上的所有字段
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        return reflection.getDeclaredFields(clazz);
    }

    /**
     * 获取类上所有的方法
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return reflection.getDeclaredMethods(clazz);
    }

    /**
     * 获取类的名字
     * */
    public static String getClassName(Class<?> clazz) {
        return reflection.getClassName(clazz);
    }
}
