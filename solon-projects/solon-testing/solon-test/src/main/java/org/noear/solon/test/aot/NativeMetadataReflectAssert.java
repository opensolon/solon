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
package org.noear.solon.test.aot;

import org.noear.solon.aot.hint.ExecutableHint;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.aot.hint.ReflectionHints;
import org.noear.solon.core.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author songyinyin
 * @since 2023/10/29 17:16
 */
public class NativeMetadataReflectAssert extends RuntimeNativeMetadataAssert {

    /**
     * 断言 native metadata 中是否注册了此类
     */
    public void hasType(Class<?> type) {
        assertTrue(hasType0(type), String.format("type '%s' not found in native metadata", type.getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此类的成员
     *
     * @param type             类
     * @param memberCategories 成员类型
     */
    public void hasMemberCategories(Class<?> type, MemberCategory... memberCategories) {
        notEmpty(memberCategories, "'memberCategories' must not be empty");

        assertTrue(getMetadata().getReflection().get(ReflectUtil.getClassName(type)).getMemberCategories().containsAll(Arrays.asList(memberCategories)),
                "memberCategories not all found in native metadata");
    }

    /**
     * 断言 native metadata 中是否注册了此类的成员
     *
     * @param className        类名
     * @param memberCategories 成员类型
     */
    public void hasMemberCategories(String className, MemberCategory... memberCategories) {
        notEmpty(memberCategories, "'memberCategories' must not be empty");

        assertTrue(getMetadata().getReflection().get(className).getMemberCategories().containsAll(Arrays.asList(memberCategories)),
                "memberCategories not all found in native metadata");
    }

    /**
     * 断言 native metadata 中是否注册了此方法
     */
    public void hasMethod(Method method) {
        notNull(method, "'method' must not be null");

        assertTrue(hasMethod0(method), String.format("method '%s' not found in native metadata", method.getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此方法
     *
     * @param type       方法的类
     * @param methodName 方法名
     */
    public void hasMethod(Class<?> type, String methodName) {
        notNull(type, "'type' must not be null");
        hasText(methodName, "'methodName' must not be empty");

        Method method = getMethod(type, methodName);
        assertTrue(hasMethod0(method), String.format("method '%s' not found in native metadata", method.getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此方法
     *
     * @param className  方法的类
     * @param methodName 方法名
     */
    public void hasMethod(String className, String methodName) throws ClassNotFoundException {
        hasText(className, "'className' must not be empty");
        hasText(methodName, "'methodName' must not be empty");

        hasMethod(Class.forName(className), methodName);
    }

    /**
     * 断言 native metadata 中是否注册了此字段
     *
     * @param field 字段
     */
    public void hasField(Field field) {
        notNull(field, "'field' must not be null");

        assertTrue(hasField0(field), String.format("field '%s' on class %s not found in native metadata", field.getName(), field.getDeclaringClass().getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此字段
     *
     * @param type      字段的类
     * @param fieldName 字段名
     */
    public void hasField(Class<?> type, String fieldName) {
        notNull(type, "'type' must not be null");
        hasText(fieldName, "'fieldName' must not be empty");

        Field field = findField(type, fieldName);
        notNull(field, String.format("field '%s' not found in class %s", fieldName, type.getName()));

        assertTrue(hasField0(field), String.format("field '%s' on class %s not found in native metadata", field.getName(), field.getDeclaringClass().getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此字段
     *
     * @param className 字段的类
     * @param fieldName 字段名
     */
    public void hasField(String className, String fieldName) throws ClassNotFoundException {
        hasText(className, "'className' must not be empty");
        hasText(fieldName, "'fieldName' must not be empty");

        hasField(Class.forName(className), fieldName);
    }

    public boolean hasType0(Class<?> type) {
        notNull(type, "'type' must not be null");

        ReflectionHints reflectionHints = getMetadata().getReflection().get(ReflectUtil.getClassName(type));
        return reflectionHints != null;
    }


    /**
     * 判断 native metadata 中是否注册了此方法
     */
    private boolean hasMethod0(Method method) {
        if (method == null) {
            return false;
        }

        ReflectionHints reflectionHints = getMetadata().getReflection().get(ReflectUtil.getClassName(method.getDeclaringClass()));
        if (reflectionHints == null) {
            return false;
        }
        if (reflectionHints.getMethods() == null) {
            return false;
        }
        return reflectionHints.getMethods().stream().anyMatch(executableHint -> testMethodEquals(executableHint, method));
    }

    public boolean hasField0(Field field) {
        if (field == null) {
            return false;
        }
        ReflectionHints reflectionHints = getMetadata().getReflection().get(ReflectUtil.getClassName(field.getDeclaringClass()));
        if (reflectionHints == null) {
            return false;
        }
        if (reflectionHints.getFields() == null) {
            return false;
        }
        return reflectionHints.getFields().contains(field.getName());
    }

    public boolean hasConstructor(Constructor<?> constructor) {
        if (constructor == null) {
            return false;
        }
        ReflectionHints reflectionHints = getMetadata().getReflection().get(ReflectUtil.getClassName(constructor.getDeclaringClass()));
        if (reflectionHints == null) {
            return false;
        }
        if (reflectionHints.getConstructors() == null) {
            return false;
        }
        return reflectionHints.getConstructors().stream().anyMatch(executableHint -> testMethodEquals(executableHint, constructor));
    }

    private boolean testMethodEquals(ExecutableHint executableHint, Executable executable) {
        boolean nameIsEqual;
        if (executable instanceof Constructor) {
            nameIsEqual = executableHint.getName().equals("<init>");
        } else {
            nameIsEqual = executableHint.getName().equals(executable.getName());
        }
        if (!nameIsEqual) {
            return false;
        }
        if (executableHint.getParameterTypes() == null && executable.getParameterCount() == 0) {
            return true;
        }
        if (executableHint.getParameterTypes().size() == executable.getParameterCount()) {
            for (int i = 0; i < executableHint.getParameterTypes().size(); i++) {
                if (!executableHint.getParameterTypes().get(i).equals(ReflectUtil.getClassName(executable.getParameterTypes()[i].getDeclaringClass()))) {
                    return false;
                }
            }
            return true;
        }
        return false;

    }
}
