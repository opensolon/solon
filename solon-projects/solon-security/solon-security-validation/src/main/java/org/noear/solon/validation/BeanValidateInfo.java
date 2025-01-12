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
package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 实体验证结果信息
 *
 * @author noear
 * @since 1.5
 */
public class BeanValidateInfo extends Result {
    /**
     * 验证执行对应的注解
     * */
    public final Annotation anno;
    /**
     * 验证结果消息
     * */
    public final String message;

    /**
     * 验证结果对应的验证名称
     * 可能是实体里的字段名称，也可能是参数里的参数名称
     */
    public final String name;

    public BeanValidateInfo(String name, Annotation anno, String message) {
        this.name = name;
        this.anno = anno;
        this.message = message;
    }
}
