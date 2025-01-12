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
 * 导入器，通过注解导入类或者包（最终作用在app source 或 config 上有效）
 *
 * <pre><code>
 * //注解传导示例
 * @Retention(RetentionPolicy.RUNTIME)
 * @Target(ElementType.TYPE)
 * @Import(value = DubboConfiguration.class)
 * public @interface EnableDubbo {
 *     ...
 * }
 *
 * //注解在应用上
 * @Import(value = DemoConfiguration.class)
 * @EnableDubbo
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args);
 *     }
 * }
 *
 * //::Import 注解在应用上的执行顺位：Plugin -> Import -> Scan bean
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
    /**
     * 导入类（beanMake）
     */
    @Alias("classes")
    Class<?>[] value() default {};

    /**
     * 导入类（beanMake）
     *
     * @since 2.5
     */
    @Alias("value")
    Class<?>[] classes() default {};

    /**
     * 扫描包（beanScan）
     */
    String[] scanPackages() default {};

    /**
     * 扫描包（beanScan）
     */
    Class<?>[] scanPackageClasses() default {};

    /**
     * 导入配置文件（classpath:demo.yml, ./demo.yml）
     *
     * @since 2.5
     */
    String[] profiles() default {};

    /**
     * 导入配置文件如果没有（classpath:demo.yml, ./demo.yml）
     *
     * @since 2.5
     */
    String[] profilesIfAbsent() default {};
}
