/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.solon.boot;
import net.hasor.core.Module;
import org.noear.solon.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Hasor
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = HasorConfiguration.class)
public @interface EnableHasor {
    /**
     * 扫描范围，配置的 scanPackages，会交给Solon进行手动扫描。
     */
    String[] scanPackages() default {};

    /**
     * Hasor 的主配置文件，可以是 Xml 或者 属性文件
     */
    String mainConfig() default "";

    /**
     * 是否将 Hasor 环境变量用作 Settings
     * （Hasor 会自动将Solon 的属性文件导入到环境变量中若想要进一步在 Settings 中使用 Solon 的属性文件就要设置为 true）
     */
    boolean useProperties() default false;

    /**
     * 启动入口
     */
    Class<? extends Module>[] startWith() default {};

    /**
     * Hasor 使用的特殊属性，这些属性会设置到 Hasor 的环境变量上
     */
    Property[] customProperties() default {};
}
