package org.noear.solon.extend.impl;

import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.core.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射扩展，native 运行时，优先从元数据文件（reflect-config.json）里获取
 *
 * @author songyinyin
 * @since 2.2
 */
public class ReflectionExt extends Reflection {

    /**
     * 获取类申明的字段
     */
    @Override
    public Field[] getDeclaredFields(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getDeclaredFields(clazz);
        } else {
            return super.getDeclaredFields(clazz);
        }
    }

    /**
     * 获取类申明的方法
     */
    @Override
    public Method[] getDeclaredMethods(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getDeclaredMethods(clazz);
        } else {
            return super.getDeclaredMethods(clazz);
        }
    }

    /**
     * 获取类所有公有的方法（包括父级）
     *
     * @since 2.5
     */
    @Override
    public Method[] getMethods(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getMethods(clazz);
        } else {
            return super.getMethods(clazz);
        }
    }
}
