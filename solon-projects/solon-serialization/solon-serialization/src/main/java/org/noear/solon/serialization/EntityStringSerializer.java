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
package org.noear.solon.serialization;

import org.noear.solon.core.convert.Converter;

/**
 * 通用上下文接口字符串序列化器
 *
 * @author noear
 * @since 3.6
 */
public interface EntityStringSerializer extends EntitySerializer<String> {
    /**
     * 添加数据转换器（用于简单场景）
     */
    <T> void addEncoder(Class<T> clz, Converter<T, Object> converter);
}
