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
package org.noear.nami;

import java.lang.reflect.Type;

/**
 * Nami - 解码器
 *
 * @author noear
 * @since 1.2
 * */
public interface Decoder {
    /**
     * 编码
     */
    String enctype();

    /**
     * 反序列化
     */
    <T> T decode(Result rst, Type clz) throws Throwable;

    /**
     * 预处理
     */
    void pretreatment(Context ctx);
}
