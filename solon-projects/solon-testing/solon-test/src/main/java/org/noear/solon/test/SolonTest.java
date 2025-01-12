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
package org.noear.solon.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

@ExtendWith(SolonJUnit5Extension.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface SolonTest {
    /**
     * 启动类
     */
    @Alias("classes")
    Class<?> value() default Void.class;

    /**
     * 启动类
     */
    @Alias("value")
    Class<?> classes() default Void.class;

    /**
     * 延迟秒数
     */
    int delay() default 1;

    /**
     * 环境配置
     */
    String env() default "";

    /**
     * 启动参数（例：--app.name=demoapp）
     */
    String[] args() default {};

    /**
     * 应用属性（例：solon.app.name=demoapp）
     */
    String[] properties() default {};

    /**
     * 是否调试模式
     */
    boolean debug() default true;

    /**
     * 是否扫描
     */
    boolean scanning() default true;

    /**
     * 是否启用 http
     */
    boolean enableHttp() default false;

    /**
     * 是否为 aot 运行
     */
    boolean isAot() default false;
}
