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

    @Override
    public Field[] getDeclaredFields(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getDeclaredFields(clazz);
        } else {
            return super.getDeclaredFields(clazz);
        }
    }

    @Override
    public Method[] getDeclaredMethods(Class<?> clazz) {
        if (NativeDetector.inNativeImage()) {
            return GraalvmUtil.getDeclaredMethods(clazz);
        } else {
            return super.getDeclaredMethods(clazz);
        }
    }
}
