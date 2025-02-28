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
package org.noear.solon.ai.embedding;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 嵌入选项
 *
 * @author noear
 * @since 3.1
 */
public class EmbeddingOptions {
    public static EmbeddingOptions of() {
        return new EmbeddingOptions();
    }


    private Map<String, Object> options = new LinkedHashMap<>();

    /**
     * 所有选项
     */
    public Map<String, Object> options() {
        return options;
    }

    /**
     * 选项获取
     */
    public Object option(String key) {
        return options.get(key);
    }

    /**
     * 选项添加
     */
    public EmbeddingOptions optionAdd(String key, Object val) {
        options.put(key, val);
        return this;
    }

    /**
     * 维度
     *
     * @param dimensions [1024、768、512]
     */
    public EmbeddingOptions dimensions(int dimensions) {
        return optionAdd("dimensions", dimensions);
    }


    /**
     * 用户
     */
    public EmbeddingOptions user(String user) {
        return optionAdd("user", user);
    }
}
