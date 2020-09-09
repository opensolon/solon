package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * Web 组件（控制器）
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XController {
}
