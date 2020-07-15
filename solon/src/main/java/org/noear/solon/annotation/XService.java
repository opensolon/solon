package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用组件
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XService {
    boolean remoting() default false; //是否开始远程服务
}
