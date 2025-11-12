/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.extend.impl;

import org.noear.solon.core.runtime.RuntimeDetector;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.core.util.Reflection;

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
        if (RuntimeDetector.inNativeImage()) {
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
        if (RuntimeDetector.inNativeImage()) {
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
        if (RuntimeDetector.inNativeImage()) {
            return GraalvmUtil.getMethods(clazz);
        } else {
            return super.getMethods(clazz);
        }
    }
}
