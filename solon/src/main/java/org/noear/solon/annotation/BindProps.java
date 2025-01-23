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
 * 绑定属性集
 *
 * 绑定类型对应的属性集（配合 @Configuration 使用有效，且用于 APT 生成配置元信息）
 *
 * <pre><code>
 * //注解在@Configuration类上
 * @BindProps(prefix="db1")
 * @Configuration
 * public class Db1Model{
 *     public String jdbcUrl;
 *     public String username;
 *     public String passwrod;
 *     public String driverClassName;
 * }
 * </code></pre>
 *
 * @author noear
 * @since 3.0
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindProps {
    String prefix() default "";
}
