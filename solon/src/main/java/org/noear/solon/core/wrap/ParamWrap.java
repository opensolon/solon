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
    private String headerName;
    private String attrName;

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
            headerName = paramAnno.headerName();
            attrName = paramAnno.attrName();
        }
    }

    public Parameter getParameter() {
        return parameter;
    }

    public String getName() {
        return name;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getAttrName() {
        return attrName;
    }

    public Class<?> getType() {
        return parameter.getType();
    }

    public boolean getRequired() {
        return required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }


}
