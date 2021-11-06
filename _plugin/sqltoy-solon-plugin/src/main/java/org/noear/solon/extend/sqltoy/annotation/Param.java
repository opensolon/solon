package org.noear.solon.extend.sqltoy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于Mapper接口中标注参数，没有使用该注解时，
 * sql中使用的参数从最后一个Map或者Entity参数中获取
 * 当有任意参数使用了Param注解后，忽略其他不带Param注解的参数(Page除外)
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    //参数名
    String value() default "";
}
