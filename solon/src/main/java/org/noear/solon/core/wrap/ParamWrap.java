package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.Context;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.2
 * @since 1.6
 */
public class ParamWrap {
    private final Parameter parameter;
    private String name;
    private String defaultValue;
    private boolean required;

    private boolean requireBody;
    private boolean isRequestHeader;
    private boolean isRequestCookie;

    private ParameterizedType genericType;

    public ParamWrap(Parameter parameter) {
        this.parameter = parameter;
        this.name = parameter.getName();

        if (resolveBody() == false) {
            if (resolveParam() == false) {
                if (resolvePathVar() == false) {
                    if (resolveHeader() == false) {
                        resolveCookie();
                    }
                }
            }
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

    /**
     * 获取参数名
     * */
    public String getName() {
        return name;
    }

    /**
     * 获取参数值
     * */
    public String getValue(Context ctx) {
        if (isRequestHeader) {
            return ctx.header(getName());
        } else if (isRequestCookie) {
            return ctx.cookie(getName());
        } else {
            return ctx.param(getName());
        }
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

    public boolean requireBody() {
        return requireBody;
    }

    /**
     * 默认值
     */
    public String defaultValue() {
        return defaultValue;
    }

    /**
     * 分析 body 注解
     */
    private boolean resolveBody() {
        Body bodyAnno = parameter.getAnnotation(Body.class);

        if (bodyAnno == null) {
            return false;
        }

        requireBody = true;
        return true;
    }

    /**
     * 分析 param 注解
     */
    private boolean resolveParam() {
        Param paramAnno = parameter.getAnnotation(Param.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(paramAnno.defaultValue()) == false) {
            defaultValue = paramAnno.defaultValue();
        }

        required = paramAnno.required();

        return true;
    }

    private boolean resolvePathVar() {
        PathVar paramAnno = parameter.getAnnotation(PathVar.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        required = true;
        return true;
    }

    /**
     * 分析 header 注解
     */
    private boolean resolveHeader() {
        Header headerAnno = parameter.getAnnotation(Header.class);

        if (headerAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(headerAnno.value(), headerAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(headerAnno.defaultValue()) == false) {
            defaultValue = headerAnno.defaultValue();
        }

        required = headerAnno.required();
        isRequestHeader = true;

        return true;
    }

    /**
     * 分析 cookie 注解
     */
    private boolean resolveCookie() {
        Cookie cookieAnno = parameter.getAnnotation(Cookie.class);

        if (cookieAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(cookieAnno.value(), cookieAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(cookieAnno.defaultValue()) == false) {
            defaultValue = cookieAnno.defaultValue();
        }

        required = cookieAnno.required();
        isRequestCookie = true;

        return true;
    }
}