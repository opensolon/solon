package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Body;
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
    private String name;
    private String defaultValue;
    private boolean required;
    private boolean requireBody;
    private ParameterizedType genericType;

    public ParamWrap(Parameter parameter) {
        this.parameter = parameter;
        this.name = parameter.getName();

        Param paramAnno = parameter.getAnnotation(Param.class);
        Body bodyAnno = parameter.getAnnotation(Body.class);

        if (paramAnno != null) {
            String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
            if (Utils.isNotEmpty(name2)) {
                name = name2;
            }

            if (Constants.PARM_UNDEFINED_VALUE.equals(paramAnno.defaultValue()) == false) {
                defaultValue = paramAnno.defaultValue();
            }

            required = paramAnno.required();
        }

        if (bodyAnno != null) {
            requireBody = true;
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

    public boolean requireBody(){
        return requireBody;
    }

    public String defaultValue() {
        return defaultValue;
    }

}
