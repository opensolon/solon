package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * Web 组件（控制器）
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XController {
}
