package org.noear.solon.web.xss.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Xss 校验注解
 *
 * @author 多仔ヾ
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Xss {

    /** 校验失败提示信息 **/
    String message() default "非法数据";

}
