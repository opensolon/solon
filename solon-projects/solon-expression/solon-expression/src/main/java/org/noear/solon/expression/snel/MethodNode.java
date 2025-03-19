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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 方法表达式节点，用于表示方法调用（如 Math.add(1, 2) 或 user.getName()）
 *
 * @author noear
 * @since 3.1
 */
public class MethodNode implements Expression {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();

    static {
        PRIMITIVE_WRAPPER_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(double.class, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(boolean.class, Boolean.class);
        // 可以添加更多的基本类型和包装类型映射
    }

    private final Expression target;    // 目标对象（如 Math 或 user）
    private final String methodName;    // 方法名（如 add 或 getName）
    private final List<Expression> args; // 方法参数列表

    public MethodNode(Expression target, String methodName, List<Expression> args) {
        this.target = target;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public Object eval(Function context) {
        // 先求值 target
        Object targetValue = target.eval(context);
        if (targetValue == null) {
            return null; // 目标为 null 时返回 null
        }

        // 获取方法参数值
        Object[] argValues = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            argValues[i] = args.get(i).eval(context);
        }

        try {
            Class<?> targetClass;
            if (targetValue instanceof Class<?>) {
                targetClass = (Class<?>) targetValue;
            } else {
                targetClass = targetValue.getClass();
            }

            // 查找方法
            Method method = findMethod(targetClass, methodName, argValues);
            if (method == null) {
                throw new EvaluationException("Method not found: " + methodName);
            }

            // 调用方法
            if (targetValue instanceof Class<?>) {
                //静态方法
                return method.invoke(null, argValues);
            } else {
                return method.invoke(targetValue, argValues);
            }
        } catch (Throwable e) {
            throw new EvaluationException("Failed to invoke method: " + methodName, e);
        }
    }

    private Class<?> getEffectiveClass(Object obj) {
        if (obj == null) {
            return Void.class;
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive()) {
            return PRIMITIVE_WRAPPER_MAP.get(clazz);
        }
        return clazz;
    }

    private static final ReflectionUtil methodUtil = new ReflectionUtil();


    private Method methodCached;

    private Method findMethod(Class<?> clazz, String methodName, Object[] argValues) {
        if (methodCached == null) {
            Class<?>[] argTypes = new Class<?>[argValues.length];
            for (int i = 0; i < argValues.length; i++) {
                argTypes[i] = getEffectiveClass(argValues[i]);
            }

            methodCached = methodUtil.getMethod(clazz, methodName, argTypes);
        }

        return methodCached;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(target);
        buf.append(".");
        buf.append(methodName);
        buf.append("(");

        for (Expression arg1 : args) {
            buf.append(arg1).append(",");
        }

        if (args.size() > 0) {
            buf.setLength(buf.length() - 1);
        }
        buf.append(")");

        return buf.toString();
    }
}