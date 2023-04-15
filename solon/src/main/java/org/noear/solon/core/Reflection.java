package org.noear.solon.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author songyinyin
 * @since 2023/4/15 12:03
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

    public String getClassName(Class<?> clazz) {
        return clazz.getCanonicalName();
    }

}
