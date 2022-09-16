package org.noear.nami.annotation;

import java.lang.annotation.*;

/**
 * 用于忽略对自身过滤方法的调用
 * @author 夜の孤城
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {
}
