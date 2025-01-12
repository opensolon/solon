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
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.TreeMap;

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
                Modifier.isPrivate(modifiers) ||
                Modifier.isProtected(modifiers)) {
            //静态 或 只读 或 私有；不需要重写
            return false;
        } else {
            return true;
        }
    }

    /**
     * 构建函数唯一识别键
     */
    public static String buildMethodKey(Method method) {
        StringBuilder buf = new StringBuilder();
        buf.append(method.getName());
        buf.append("(");

        for (Parameter pe : method.getParameters()) {
            Class<?> pet = pe.getType();//todo: 可能要增加泛型支持

            buf.append(pet.toString());
            buf.append(",");
        }

        buf.append(")");

        return buf.toString();
    }

    /**
     * 查找类的所有函数（包括父类）
     *
     * @param type 类型
     */
    public static Map<String, Method> findMethodAll(Class<?> type) {
        Map<String, Method> methodAll = new TreeMap<>(); //new LinkedHashMap<>();

        findMethodDo(type, methodAll);

        return methodAll;
    }

    /**
     * 查找函数
     */
    private static void findMethodDo(Class<?> type, Map<String, Method> methodAll) {
        if (type == null || type == Object.class) {
            return;
        }

        //本级优先
        for (Method method : type.getDeclaredMethods()) {
            if (allowMethod(method)) {
                String methodKey = buildMethodKey(method);
                methodAll.putIfAbsent(methodKey, method);
            }
        }

        findMethodDo(type.getSuperclass(), methodAll);

        for (Class<?> ifType : type.getInterfaces()) {
            findMethodDo(ifType, methodAll);
        }
    }
}