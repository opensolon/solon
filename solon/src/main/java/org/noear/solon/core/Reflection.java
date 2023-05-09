package org.noear.solon.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射
 *
 * @author songyinyin
 * @since 2.2
 */
public class Reflection {

    /**
     * 获取类上的所有字段
     */
    public Field[] getDeclaredFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * 获取类上所有的方法
     */
    public Method[] getDeclaredMethods(Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }

    /**
     * 获取类的名字
     * */
    public String getClassName(Class<?> clazz) {
        return clazz.getCanonicalName();
    }

}
