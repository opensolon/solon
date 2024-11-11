/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.data.sqlink.annotation;

import org.noear.solon.data.sqlink.base.generate.DynamicGenerator;
import org.noear.solon.data.sqlink.base.generate.NoGenerator;

import java.lang.annotation.*;

/**
 * 插入时默认值注解
 *
 * @author kiryu1223
 * @since 3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InsertDefaultValue {
    /**
     * 生成策略
     * <p>
     * <b>注意：在选择数据库生成策略情况下，插入时无论该字段是否有值，都会被忽略</b>
     */
    GenerateStrategy strategy();

    /**
     * 静态值
     */
    String value() default "";

    /**
     * 动态值
     */
    Class<? extends DynamicGenerator<?>> dynamic() default NoGenerator.class;
}
