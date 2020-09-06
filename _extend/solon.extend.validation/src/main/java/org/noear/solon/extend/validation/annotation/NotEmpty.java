package org.noear.solon.extend.validation.annotation;


import org.noear.solon.annotation.XNote;

import java.lang.annotation.*;

/**
 * 不能为空
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {
    /**
     * param names
     * */
    @XNote("param names")
    String[] names();
}
