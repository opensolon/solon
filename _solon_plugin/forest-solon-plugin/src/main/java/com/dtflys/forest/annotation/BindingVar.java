package com.dtflys.forest.annotation;

import java.lang.annotation.*;

/**
 * Forest 全局变量绑定标签
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindingVar {

    /**
     * 绑定的变量名
     * @return 绑定的变量名
     */
    String value();

    /**
     * 所绑定的 ForestConfiguration Bean Id
     * @return 所绑定的 ForestConfiguration Bean Id
     */
    String configuration() default "";

}
