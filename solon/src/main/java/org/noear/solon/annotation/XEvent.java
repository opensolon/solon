package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 事件订阅（注解在XEventListener的实现类上，才有效）
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XEvent {
    Class<?> value();
}
