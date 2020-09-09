package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 单例
 *
 * 一般附加在XController上；可继承
 *
 * @author noear
 * @since 1.0
 * */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSingleton {
    boolean value();
}