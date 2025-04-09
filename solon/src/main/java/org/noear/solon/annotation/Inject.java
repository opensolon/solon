/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 *
 * 可注入到字段或参数或类型（类型和参数只在@Configuration有效）
 *
 * 禁止注入在类型上；可避免让非单例bean的注入变复杂，进而避免影响性能
 *
 * <pre>{@code
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
 * }</pre>
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
    @Note("配置注入时有效")
    boolean required() default true;
    /**
     * 自动刷新
     * */
    @Note("配置注入时有效，且单例才有自动刷新的必要")
    boolean autoRefreshed() default false;
}
