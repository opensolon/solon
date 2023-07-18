package org.noear.solon.core.wrap;

import org.noear.solon.core.handle.Context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 变量描述符
 *
 * @author noear
 * @since 2.3
 */
public interface VarDescriptor {
    /**
     * 必须有 body
     */
    boolean isRequiredBody();

    /**
     * 必须有 header
     */
    boolean isRequiredHeader();

    /**
     * 必须有 cokkie
     */
    boolean isRequiredCookie();

    /**
     * 必须有 path
     */
    boolean isRequiredPath();

    /**
     * 必须有输入
     */
    boolean isRequiredInput();

    /**
     * 获取必须缺失时的提示
     */
    String getRequiredHint();

    /**
     * 获取名字
     */
    String getName();

    /**
     * 获取默认值
     * */
    String getDefaultValue();

    Type getGenericType();

    default boolean isGenericType(){
        return getGenericType() instanceof ParameterizedType;
    }

    Class<?> getType();


    /**
     * 获取参数值
     */
    default String getValue(Context ctx) {
        if (isRequiredHeader()) {
            return ctx.header(getName());
        } else if (isRequiredCookie()) {
            return ctx.cookie(getName());
        } else {
            return ctx.param(getName());
        }
    }

    /**
     * 获取值
     * */
    default String[] getValues(Context ctx) {
        if (isRequiredHeader()) {
            return ctx.headerValues(getName());
        } else if (isRequiredCookie()) {
            return new String[]{ctx.cookie(getName())};
        } else {
            return ctx.paramValues(getName());
        }
    }
}
