package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 事件订阅
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XEvent {
    Class<?> value();
}
