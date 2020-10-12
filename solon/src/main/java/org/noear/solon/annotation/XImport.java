package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 导入器，通过注解必入bean（只在app source上有效）
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XImport {
    Class<?>[] value() default {};
    String[] basePackages() default {};
}
