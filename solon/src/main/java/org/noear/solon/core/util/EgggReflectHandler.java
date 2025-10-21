package org.noear.solon.core.util;


import org.noear.eggg.ReflectHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author noear 2025/10/21 created
 * @since 3.7
 */
public class EgggReflectHandler implements ReflectHandler {
    @Override
    public Field[] getDeclaredFields(Class<?> clazz) {
        return ReflectUtil.getDeclaredFields(clazz);
    }

    @Override
    public Method[] getDeclaredMethods(Class<?> clazz) {
        return ReflectUtil.getDeclaredMethods(clazz);
    }

    @Override
    public Method[] getMethods(Class<?> clazz) {
        return ReflectUtil.getMethods(clazz);
    }
}
