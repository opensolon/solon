package org.noear.solon.extend.async.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 异步执行注解
 *
 * @author noear
 * @since 1.6
 */
@Note("由插件 solon.scheduling 替代")
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {
}
