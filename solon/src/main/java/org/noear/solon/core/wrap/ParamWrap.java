package org.noear.solon.core.wrap;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.2
 * @since 1.6
 * @since 2.4
 */
public class ParamWrap extends VarDescriptorBase {
    private final Parameter parameter;

    public ParamWrap(Parameter parameter) {
        super(parameter, parameter.getName());
        this.parameter = parameter;
        this.init();
    }

    /**
     * 获取原始参数
     */
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public Type getGenericType() {
        return parameter.getParameterizedType();
    }

    @Override
    public Class<?> getType() {
        return parameter.getType();
    }
}