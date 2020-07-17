package org.noear.solon.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 初始化（相当于 PostConstruct）
 *
 * Bean 构建过程：Constructor(构造方法) -> @XInject(依赖注入) -> @XInit(初始化)
 * */
@Retention (RUNTIME)
@Target(METHOD)
@Documented
public @interface XInit {
}
