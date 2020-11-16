package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用 组件
 *
 * <pre><code>
 * //注解在类上
 * @XBean
 * public class DemoBean{
 *     @XInject
 *     DataSource db1;
 * }
 *
 * //注解在配置器的函数上
 * @XConfiguration
 * public class Config{
 *     @XBean
 *     public DataSource db1(@XInject("${db1}") HikariDataSource ds){
 *         return ds;
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    @Note("注册名称")
    String value() default ""; //as bean.name

    @Note("标签，用于快速查找")
    String tag() default "";

    @Note("特性，用于辅助配置")
    String attrs() default "";

    @Note("同时注册类型，仅当名称非空时有效")
    boolean typed() default false;

    boolean remoting() default false; //是否开始远程服务
}
