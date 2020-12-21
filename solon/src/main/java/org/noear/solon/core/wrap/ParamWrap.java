package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Param;
import org.noear.solon.core.Constants;

import java.lang.reflect.Parameter;

/**
 * @author noear 2020/12/20 created
 */
public class ParamWrap {
    private final Parameter parameter;
    private final Param paramAnno;
    private String name;
    private String defaultValue;
    private boolean required;
    private boolean useHeader;
    private boolean useAttr;

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
            useHeader = paramAnno.useHeader();
            useAttr = paramAnno.useAttr();
        }
    }

    public Parameter getParameter() {
        return parameter;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return parameter.getType();
    }

    public boolean required() {
        return required;
    }

    public boolean useHeader() {
        return useHeader;
    }

    public boolean useAttr() {
        return useAttr;
    }

    public String defaultValue() {
        return defaultValue;
    }

}
