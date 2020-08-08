package org.noear.solon.extend.mybatis;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Df {
    String value();
}
