package org.noear.solon.data.sqlink.base.annotation;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定使用的TypeHandler，此处指定的TypeHandler优先级高于根据类型匹配的
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UseTypeHandler
{
    /**
     * 使用的TypeHandler的实现类（必须完全实现泛型）
     */
    Class<? extends ITypeHandler<?>> value();
}
