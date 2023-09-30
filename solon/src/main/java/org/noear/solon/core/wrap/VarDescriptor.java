package org.noear.solon.core.wrap;

import org.noear.solon.core.handle.Context;
import org.noear.solon.lang.Nullable;

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
     * 必须有 body（一般是指用了 @Body 注解）
     */
    boolean isRequiredBody();

    /**
     * 必须有 header（一般是指用了 @Header 注解）
     */
    boolean isRequiredHeader();

    /**
     * 必须有 cookie（一般是指用了 @Cookie 注解）
     */
    boolean isRequiredCookie();

    /**
     * 必须有 path（一般是指用了 @Path 注解）
     */
    boolean isRequiredPath();

    /**
     * 必须有输入（一般是指注解里 required = true）
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

    /**
     * 获取泛型
     * */
    @Nullable
    Type getGenericType();

    /**
     * 是否为泛型
     * */
    default boolean isGenericType(){
        return getGenericType() instanceof ParameterizedType;
    }

    /**
     * 获取类型
     * */
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
