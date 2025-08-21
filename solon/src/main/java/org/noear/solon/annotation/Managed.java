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
 * 托管
 *
 * <pre>{@code
 * //注解在类上（生产托管 Bean，支持动态代理机制）
 * @Managed
 * public class DemoBean{
 *     @Inject
 *     DataSource db1;
 * }
 *
 * @Configuration
 * public class Config{
 *     //注解在方法上（构建托管 Bean）
 *     @Managed
 *     public DataSource db1(@Inject("${db1}") HikariDataSource ds){
 *         return ds;
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 3.4
 * @since 3.5
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Preview("3.4")
public @interface Managed {
    @Alias("name")
    String value() default ""; //as bean.name

    @Alias("value")
    String name() default "";

    /**
     * 标签，用于查找
     */
    String tag() default "";

    /**
     * 同时注册类型，仅当名称非空时有效
     */
    boolean typed() default false;

    /**
     * 排序（产生之后的同类组件顺序）
     */
    int index() default 0;

    /**
     * 要交付的（特定能力接口交付）
     */
    boolean delivered() default true;

    /// ///////////////


    /**
     * 初始化方法（用于托管方法时）
     */
    String initMethod() default "";

    /**
     * 注销方法（用于托管方法时）
     */
    String destroyMethod() default "";

    /**
     * 自动注入（用于托管方法时）
     *
     * @since 3.5
     */
    @Preview("3.5")
    boolean autoInject() default false;
}