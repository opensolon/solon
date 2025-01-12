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

import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Result;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 验证器异常
 *
 * @author noear
 * @since 1.4
 */
public class ValidatorException extends StatusException {
    @Nullable
    private Method method;
    private Annotation annotation;
    private Result result;

    /**
     * 获取触发函数
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取触发的注解
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * 获取结果
     */
    public Result getResult() {
        return result;
    }

    public ValidatorException(int code, String message, Annotation annotation, Result result) {
        this(code, message, annotation, result, null);
    }

    public ValidatorException(int code, String message, Annotation annotation, Result result, Method method) {
        super(message, code);
        this.method = method;
        this.annotation = annotation;
        this.result = result;
    }
}