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
     * 获取类的名字
     */
    public static String getClassName(Class<?> clazz) {
        return reflection.getClassName(clazz);
    }

    /**
     * 获取类的自己申明的字段
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        return reflection.getDeclaredFields(clazz);
    }

    /**
     * 获取类的自己申明的方法
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return reflection.getDeclaredMethods(clazz);
    }

    /**
     * 获取类的所有公有方法（包括父类）
     */
    public static Method[] getMethods(Class<?> clazz) {
        return reflection.getMethods(clazz);
    }
}
