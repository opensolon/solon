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

    static Reflection global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.ReflectionExt");

        if (global == null) {
            global = new Reflection();
        }
    }

    /**
     * 获取类的名字
     */
    public static String getClassName(Class<?> clazz) {
        return global.getClassName(clazz);
    }

    /**
     * 获取类的自己申明的字段
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        return global.getDeclaredFields(clazz);
    }

    /**
     * 获取类的自己申明的方法
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return global.getDeclaredMethods(clazz);
    }

    /**
     * 获取类的所有公有方法（包括父类）
     */
    public static Method[] getMethods(Class<?> clazz) {
        return global.getMethods(clazz);
    }
}
