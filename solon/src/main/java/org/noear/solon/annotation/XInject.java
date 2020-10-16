package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 *
 * 可注入到字段或参数或类型（类型和参数只在XConfiguration有效）
 *
 * 禁止注入在类型上；可避免让非单例bean的注入变复杂，进而避免影有响性能
 *
 * <pre><code>
 * //注解在bean的字段上
 * @XBean
 * public class DemoBean{
 *     @XInject
 *     UserService userService;
 *
 *     @XInject
 *     Db1Model db1Config;
 * }
 *
 * //注解在@XConfiguration类上
 * @XInject("${db1}")
 * @XConfiguration
 * public class Db1Model{
 *     public String jdbcUrl;
 *     public String username;
 *     public String passwrod;
 *     public String driverClassName;
 * }
 *
 * //注解在@XConfiguration的Bean构建参数上
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
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInject {
    String value() default "";
}
