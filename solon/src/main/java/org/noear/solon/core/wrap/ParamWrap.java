package org.noear.solon.core.wrap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * 参数包装
 *
 * @author noear
 * @since 1.2
 * @since 1.6
 * @since 2.4
 */
public class ParamWrap extends VarDescriptorBase {
    private final Parameter parameter;
    private Class<?> type;

    public ParamWrap(Parameter parameter) {
        this(parameter, null, null);
    }

    public ParamWrap(Parameter parameter, Method method, Map<String, Type> genericInfo) {
        super(parameter, parameter.getName());
        this.parameter = parameter;
        this.type = parameter.getType();
        this.init();

        if (genericInfo != null && getGenericType() instanceof TypeVariable) {
            Type ptTmp = genericInfo.get(getGenericType().getTypeName());

            if (ptTmp instanceof Class) {
                type = (Class<?>) ptTmp;
            } else {
                throw new IllegalStateException("Mapping mehtod generic analysis error: "
                        + method.getDeclaringClass().getName()
                        + "."
                        + method.getName());
            }
        }
    }

    /**
     * 获取原始参数
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * 获取泛型
     */
    @Override
    public Type getGenericType() {
        return parameter.getParameterizedType();
    }

    /**
     * 获取类型
     */
    @Override
    public Class<?> getType() {
        return type;
    }
}