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
package org.noear.solon.validation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 验证器（对验证注解的支持）
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface Validator<T extends Annotation> {
    default String message(T anno) {
        return "";
    }

    /**
     * 校验分组
     */
    default Class<?>[] groups(T anno) {
        return null;
    }


    /**
     * 是否支持值类型
     */
    default boolean isSupportValueType(Class<?> type) {
        return true;
    }

    /**
     * 验证值
     *
     * @param anno 验证注解
     * @param val  值
     * @param tmp  临时字符构建器（用于构建 message；起到复用之效）
     * @return 验证结果
     */
    default Result validateOfValue(T anno, Object val, StringBuilder tmp) {
        return Result.failure();
    }


    /**
     * 验证上下文
     *
     * @param ctx  上下文
     * @param anno 验证注解
     * @param name 参数名（可能没有）
     * @param tmp  临时字符构建器（用于构建 message；起到复用之效）
     * @return 验证结果
     */
    Result validateOfContext(Context ctx, T anno, String name, StringBuilder tmp);
}
