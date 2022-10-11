package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 *
 * 可注入到字段或参数或类型（类型和参数只在@Configuration有效）
 *
 * 禁止注入在类型上；可避免让非单例bean的注入变复杂，进而避免影有响性能
 *
 * <pre><code>
 * //注解在bean的字段上
 * @Component
 * public class DemoBean{
 *     @Inject
 *     UserService userService;
 *
 *     @Inject
 *     Db1Model db1Config;
 * }
 *
 * //注解在@Configuration类上
 * @Inject("${db1}")
 * @Configuration
 * public class Db1Model{
 *     public String jdbcUrl;
 *     public String username;
 *     public String passwrod;
 *     public String driverClassName;
 * }
 *
 * //注解在@Configuration的Bean构建参数上
 * @Configuration
 * public class Config{
 *     @Bean
 *     public DataSource db1(@Inject("${db1}") HikariDataSource ds){
 *         return ds;
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {
    String value() default "";
    /**
     * 必需的
     * */
    @Note("配置注入时才检查")
    boolean required() default false;
    /**
     * 自动刷新
     * */
    @Note("单例才有自动刷新的必要")
    boolean autoRefreshed() default false;
}
