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
package org.noear.solon.aot.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 函数处理工具
 *
 * @author noear
 * @since 2.2
 */
public class MethodFinder {
    /**
     * 是否允许函数处理
     */
    public static boolean allowMethod(Method element) {

        int modifiers = element.getModifiers();
        if (Modifier.isStatic(modifiers) ||
                Modifier.isFinal(modifiers) ||
                Modifier.isAbstract(modifiers) ||
                Modifier.isVolatile(modifiers) || //桥接方法
                Modifier.isPrivate(modifiers) ||
                Modifier.isProtected(modifiers)) {
            //静态 或 只读 或 私有；不需要重写
            return false;
        } else {
            return true;
        }
    }

    /**
     * 查找类的所有公有函数（包括父类）
     *
     * @param type 类型
     */
    public static Collection<Method> findMethodAll(Class<?> type) {
        List<Method> methods = new ArrayList<>();

        for (Method m : type.getMethods()) {
            if (m.getDeclaringClass() != Object.class) {
                methods.add(m);
            }
        }

        return methods;
    }
}