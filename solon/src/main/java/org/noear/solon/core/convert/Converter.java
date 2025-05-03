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
package org.noear.solon.core.convert;

import org.noear.solon.core.exception.ConvertException;

/**
 * 转换器（主要用于配置或网络的单值转换）
 *
 * @author noear
 * @since 2.4
 */
@FunctionalInterface
public interface Converter<S,T> {
    /**
     * 转换
     *
     * @param value 值
     * @return 转换后的值
     * */
    T convert(S value) throws ConvertException;
}
