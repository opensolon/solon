package org.noear.solon.extend.vaptcha.http.request.validators;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author iYarnFog
 * @since 1.5
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Vaptcha {
    // 用Note注解，是为了用时还能看到这个注释
    @Note("错误信息")
    String message() default "Illegal behavior verification.";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}