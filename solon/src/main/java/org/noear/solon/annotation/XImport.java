package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 导入器，通过注解导入类或者包（只在app 或 config 有效）
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XImport {
    Class<?>[] value() default {};
    String[] scanPackages() default {};
}
