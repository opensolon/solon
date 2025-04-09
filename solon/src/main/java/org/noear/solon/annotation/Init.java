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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 初始化方法注解
 *
 * Bean 构建过程：Constructor(构造方法) -> @Inject(依赖注入) -> @Init(初始化)
 *
 * <pre>{@code
 * @Component
 * public class DemoBean{
 *     @Inject("${db1}")
 *     Properties props;
 *
 *     @Inject("${user.name}")
 *     String name;
 *
 *     public DemoBean(){
 *         //此时 props,name === null
 *     }
 *
 *     @Init
 *     public void init(){
 *         //此时 props,name !== null
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * */
@Retention (RUNTIME)
@Target(METHOD)
@Documented
public @interface Init {
    /**
     * 排序（0表示自动）
     * */
    int index() default 0;
}
