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
 * mvc::Web 组件（控制器，一般与@Mapping 配合使用）
 *
 * <pre>{@code
 * @Valid        //增加验证支持
 * @Controller
 * public class DemoController{
 *     @NotNull({"name","message"})
 *     @Mapping("/hello/")
 *     public String hello(String name, String message){
 *         return "Hello " + name;
 *     }
 *
 *     @Mapping("/cmd/{cmd}")
 *     public String cmd(@NotNull String cmd){
 *         return "cmd = " + cmd;
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    /**
     * 远程模式（可以不用加 Mapping 注解）
     *
     * @since 3.7.4
     */
    @Preview("3.7.4")
    boolean remoting() default false;
}
