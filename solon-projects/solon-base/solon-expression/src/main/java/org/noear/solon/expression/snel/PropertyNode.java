package org.noear.solon.expression.snel;

import org.noear.solon.core.util.NameUtil;
import org.noear.solon.expression.Expression;
import org.noear.solon.expression.ExpressionContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 属性表达式节点
 *
 * @author noear
 * @since 3.1
 */
public class PropertyNode implements Expression {
    private final Expression target;    // 目标对象（如 user）
    private final Expression property;  // 属性名（如 name 或 'address'）

    public PropertyNode(Expression target, String property) {
        this(target, new ConstantNode(property)); // 点号属性直接转为常量
    }

    public PropertyNode(Expression target, Expression property) {
        this.target = target;
        this.property = property;
    }

    @Override
    public Object evaluate(ExpressionContext context) {
        Object targetValue = target.evaluate(context);
        Object propertyValue = property.evaluate(context);

        if (targetValue == null) {
            return null; // 目标为 null 时返回 null，避免抛出异常
        }

        if (propertyValue == null) {
            return null; // 属性名为 null 时返回 null
        }

        String propName = propertyValue.toString();

        // 从 Map 或 Java Bean 中获取属性值
        if (targetValue instanceof Map) {
            return ((Map<?, ?>) targetValue).get(propName);
        } else {
            try {
                String getName = NameUtil.getPropGetterName(propName);
                Method method = targetValue.getClass().getMethod(getName);
                return method.invoke(targetValue);
            } catch (NoSuchMethodException e) {
                // 尝试访问公共字段
                try {
                    Field field = targetValue.getClass().getField(propName);
                    return field.get(targetValue);
                } catch (NoSuchFieldException ex) {
                    return null; // 字段不存在返回 null
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to access field: " + propName, ex);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to access property: " + propName, e);
            }
        }
    }
}