package org.noear.solon.core.wrap;

import java.lang.reflect.*;
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
    private Type genericType;

    public ParamWrap(Parameter parameter) {
        this(parameter, null, null);
    }

    public ParamWrap(Parameter parameter, Method method, Map<String, Type> genericInfo) {
        super(parameter, parameter.getName());
        this.parameter = parameter;
        this.type = parameter.getType();
        this.genericType = parameter.getParameterizedType();

        if (method != null) {
            //for action
            this.init();

            if (genericInfo != null && genericType instanceof TypeVariable) {
                Type type0 = genericInfo.get(genericType.getTypeName());

                if (type0 instanceof ParameterizedType) {
                    genericType = type0;
                    type0 = ((ParameterizedType) type0).getRawType();
                }

                if (type0 instanceof Class) {
                    type = (Class<?>) type0;
                } else {
                    throw new IllegalStateException("Mapping mehtod generic analysis error: "
                            + method.getDeclaringClass().getName()
                            + "."
                            + method.getName());
                }
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
        return genericType;
    }

    /**
     * 获取类型
     */
    @Override
    public Class<?> getType() {
        return type;
    }
}