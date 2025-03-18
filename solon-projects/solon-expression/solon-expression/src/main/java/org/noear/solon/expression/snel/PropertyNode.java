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
package org.noear.solon.expression.snel;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.exception.EvaluationException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 属性表达式节点，用于表示属性访问（如 user.name 或 order['items'][0]）
 *
 * @author noear
 * @since 3.1
 */
public class PropertyNode implements Expression {
    private final Expression target;    // 目标对象（如 user）
    private final Expression property; // 属性名或索引（如 name 或 0）

    public PropertyNode(Expression target, String property) {
        this(target, new ConstantNode(property));
    }

    public PropertyNode(Expression target, Expression property) {
        this.target = target;
        this.property = property;
    }

    @Override
    public Object eval(Function context) {
        Object targetValue = target.eval(context);
        if (targetValue == null) {
            return null; // 目标为 null 时返回 null
        }

        Object propertyValue = property.eval(context);
        if (propertyValue == null) {
            return null; // 属性为 null 时返回 null
        }

        // 处理集合类型的整数索引访问
        if (targetValue instanceof List && propertyValue instanceof Number) {
            int index = ((Number) propertyValue).intValue();
            try {
                return ((List<?>) targetValue).get(index);
            } catch (IndexOutOfBoundsException e) {
                return null; // 索引越界时返回 null
            }
        }

        // 处理数组类型的整数索引访问
        if (targetValue.getClass().isArray() && propertyValue instanceof Number) {
            int index = ((Number) propertyValue).intValue();
            try {
                return java.lang.reflect.Array.get(targetValue, index);
            } catch (ArrayIndexOutOfBoundsException e) {
                return null; // 索引越界时返回 null
            }
        }

        // 处理 Map 或 Java Bean 属性访问
        String propName = propertyValue.toString();
        if (targetValue instanceof Map) {
            return ((Map<?, ?>) targetValue).get(propName);
        } else {
            return getPropertyValue(targetValue, propName);
        }
    }

    /**
     * 获取目标对象
     */
    public Expression getTarget() {
        return target;
    }

    /**
     * 获取属性名
     */
    public String getPropertyName() {
        if (property instanceof ConstantNode) {
            return ((ConstantNode) property).getValue().toString();
        }

        throw new EvaluationException("Invalid property name: " + property);
    }

    /**
     * 获取 Java Bean 属性值
     */
    private PropertyHolder propertyCached;
    private static ReentrantLock locker = new ReentrantLock();

    private Object getPropertyValue(Object target, String propName) {
        if (propertyCached == null) {
            locker.lock();
            try {
                if (propertyCached == null) {
                    propertyCached = getPropertyDo(target, propName);
                }
            } finally {
                locker.unlock();
            }
        }

        try {
            return propertyCached.getValue(target);
        } catch (Throwable e) {
            throw new EvaluationException("Failed to access property: " + propName, e);
        }
    }

    private PropertyHolder getPropertyDo(Object target, String propName) {
        try {
            String name = "get" + capitalize(propName);
            Method method = target.getClass().getMethod(name);
            method.setAccessible(true);

            MethodHandle methodHandle = MethodHandles.lookup().unreflect(method);
            return new PropertyHolder(methodHandle, null);
        } catch (NoSuchMethodException e) {
            try {
                Field field = target.getClass().getField(propName);
                field.setAccessible(true);

                return new PropertyHolder(null, field);
            } catch (NoSuchFieldException ex) {
                throw new EvaluationException("Missing property: " + propName, e);
            }
        } catch (IllegalAccessException e) {
            throw new EvaluationException("Illegal access property: " + propName);
        }
    }

    /**
     * 将字符串首字母大写
     */
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public String toString() {
        return target + "[" + property + "]";
    }
}