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
package org.noear.nami;

/**
 * Nami - 编码器
 *
 * @author noear
 * @since 1.2
 * */
public interface Encoder {
    /**
     * 必须要 body
     */
    default boolean bodyRequired() {
        return false;
    }

    /**
     * 编码
     */
    String enctype();

    /**
     * 序列化
     */
    byte[] encode(Object obj) throws Throwable;

    /**
     * 预处理
     */
    void pretreatment(Context ctx);
}
