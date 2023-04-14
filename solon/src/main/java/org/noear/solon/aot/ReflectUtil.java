package org.noear.solon.aot;

import org.noear.solon.aot.graalvm.GraalvmUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类，会兼容 graalvm native image
 *
 * @author songyinyin
 * @since 2023/4/13 13:54
 */
public class ReflectUtil {

    public static final String CONSTRUCTOR_NAME = "<init>";

    /**
     * 获取类上的所有字段
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getDeclaredFields(clazz);
        }
        return clazz.getDeclaredFields();
    }

    /**
     * 获取类上所有的方法
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getDeclaredMethods(clazz);
        }
        return clazz.getDeclaredMethods();
    }

    public static String getClassName(Class<?> clazz) {
        return clazz.getCanonicalName();
    }
}
