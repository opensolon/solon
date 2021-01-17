package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Param;
import org.noear.solon.core.Constants;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author noear 2020/12/20 created
 */
public class ParamWrap {
    private final Parameter parameter;
    private final Param paramAnno;
    private String name;
    private String defaultValue;
    private boolean required;
    private ParameterizedType genericType;

    public ParamWrap(Parameter parameter) {
        this.parameter = parameter;
        this.paramAnno = parameter.getAnnotation(Param.class);

        this.name = parameter.getName();

        if (paramAnno != null) {
            if (Utils.isNotEmpty(paramAnno.name())) {
                name = paramAnno.name();
            }

            if (Constants.PARM_UNDEFINED_VALUE.equals(paramAnno.defaultValue()) == false) {
                defaultValue = paramAnno.defaultValue();
            }

            required = paramAnno.required();
        }

        Type tmp = parameter.getParameterizedType();
        if (tmp instanceof ParameterizedType) {
            genericType = (ParameterizedType) tmp;
        } else {
            genericType = null;
        }
    }

    public Parameter getParameter() {
        return parameter;
    }

    public String getName() {
        return name;
    }

    public ParameterizedType getGenericType() {
        return genericType;
    }

    public Class<?> getType() {
        return parameter.getType();
    }

    public boolean required() {
        return required;
    }

    public String defaultValue() {
        return defaultValue;
    }

}
