package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通过注解，导入bean
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XImport {
    Class<?>[] value();
}
