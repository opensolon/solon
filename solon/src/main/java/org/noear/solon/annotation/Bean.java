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

import org.noear.solon.lang.Preview;

import java.lang.annotation.*;

/**
 * 通用 组件（只能配合 @Configuration 使用）
 *
 * <pre>{@code
 * //注解在配置器的函数上
 * @Configuration
 * public class Config{
 *     @Managed
 *     public DataSource db1(@Inject("${db1}") HikariDataSource ds){
 *         return ds;
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    @Alias("name")
    String value() default ""; //as bean.name

    @Alias("value")
    String name() default "";

    /**
     * 标签，用于查找
     */
    String tag() default "";

    /**
     * 要类型的（同时注册类型），仅当名称非空时有效
     */
    boolean typed() default false;

    /**
     * 排序（产生之后的同类组件顺序）
     */
    int index() default 0;

    /**
     * 优先（产生之前的运行优先级；越大越优）
     *
     * @deprecated 3.5 {@link Condition#priority()}
     */
    @Deprecated
    int priority() default 0;

    /// ///////////////

    /**
     * 要交付的（特定能力接口交付）
     */
    boolean delivered() default true;

    /**
     * 初始化方法
     */
    String initMethod() default "";

    /**
     * 注销方法
     */
    String destroyMethod() default "";

    /**
     * 要注入的
     *
     * @deprecated 3.6 {@link #autoInject()}
     */
    @Deprecated
    boolean injected() default false;

    /**
     * 自动注入
     *
     * @since 3.5
     */
    @Preview("3.5")
    boolean autoInject() default false;

    /**
     * 自动代理
     *
     * @since 3.5
     */
    @Preview("3.6")
    boolean autoProxy() default false;
}
