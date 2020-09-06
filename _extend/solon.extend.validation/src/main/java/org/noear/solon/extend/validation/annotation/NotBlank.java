package org.noear.solon.extend.validation.annotation;


import org.noear.solon.annotation.XNote;

import java.lang.annotation.*;

/**
 * 不能为空白的
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {
    /**
     * param names
     * */
    @XNote("param names")
    String[] check();
}
