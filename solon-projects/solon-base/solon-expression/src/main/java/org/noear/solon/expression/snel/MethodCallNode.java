package org.noear.solon.expression.snel;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.ExpressionContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法调用节点，用于表示方法调用（如 Math.add(1, 2) 或 user.getName()）
 */
public class MethodCallNode implements Expression {
    private final Expression target;    // 目标对象（如 Math 或 user）
    private final String methodName;    // 方法名（如 add 或 getName）
    private final List<Expression> args; // 方法参数列表

    public MethodCallNode(Expression target, String methodName, List<Expression> args) {
        this.target = target;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public Object evaluate(ExpressionContext context) {
        // 先求值 target
        Object targetValue = target.evaluate(context);
        if (targetValue == null) {
            return null; // 目标为 null 时返回 null
        }

        // 获取方法参数值
        Object[] argValues = new Object[args.size()];
        Class<?>[] argTypes = new Class<?>[args.size()];
        for (int i = 0; i < args.size(); i++) {
            argValues[i] = args.get(i).evaluate(context);
            argTypes[i] = argValues[i] != null ? argValues[i].getClass() : Object.class;
        }

        try {
            // 查找方法
            Method method = findMethod(targetValue.getClass(), methodName, argTypes);
            if (method == null) {
                throw new RuntimeException("Method not found: " + methodName);
            }

            // 调用方法
            return method.invoke(targetValue, argValues);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method: " + methodName, e);
        }
    }

    /**
     * 查找匹配的方法
     */
    private Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == argTypes.length) {
                    boolean match = true;
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (!paramTypes[i].isAssignableFrom(argTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
}