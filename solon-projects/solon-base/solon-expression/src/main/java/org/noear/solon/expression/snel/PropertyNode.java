package org.noear.solon.expression.snel;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.ExpressionContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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
    public Object evaluate(ExpressionContext context) {
        Object targetValue = target.evaluate(context);
        Object propertyValue = property.evaluate(context);

        if (targetValue == null || propertyValue == null) {
            return null; // 目标或属性为 null 时返回 null
        }

        // 处理集合类型的整数索引访问
        if (targetValue instanceof List && propertyValue instanceof Number) {
            int index = ((Number) propertyValue).intValue();
            return ((List<?>) targetValue).get(index);
        }

        // 处理数组类型的整数索引访问
        if (targetValue.getClass().isArray() && propertyValue instanceof Number) {
            int index = ((Number) propertyValue).intValue();
            return java.lang.reflect.Array.get(targetValue, index);
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
     * 获取属性名
     */
    public String getPropertyName() {
        if (property instanceof ConstantNode) {
            return ((ConstantNode) property).getValue().toString();
        }
        throw new RuntimeException("Invalid property name: " + property);
    }

    /**
     * 获取 Java Bean 属性值
     */
    private Object getPropertyValue(Object target, String propName) {
        try {
            // 尝试通过 getter 方法获取属性值
            String getterName = "get" + capitalize(propName);
            Method getter = target.getClass().getMethod(getterName);
            return getter.invoke(target);
        } catch (NoSuchMethodException e) {
            // 尝试访问公共字段
            try {
                Field field = target.getClass().getField(propName);
                return field.get(target);
            } catch (Exception ex) {
                return null; // 属性不存在返回 null
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to access property: " + propName, e);
        }
    }

    /**
     * 将字符串首字母大写
     */
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}