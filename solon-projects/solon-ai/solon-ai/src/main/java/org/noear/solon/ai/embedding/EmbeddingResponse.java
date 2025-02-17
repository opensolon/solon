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

import org.noear.solon.ai.Usage;

import java.util.List;

/**
 * 嵌入响应
 *
 * @author noear
 * @since 3.1
 */
public class EmbeddingResponse {
    private final String model;
    private final List<Embedding> data;
    private final Usage usage;

    public EmbeddingResponse(String model, List<Embedding> data, Usage usage) {
        this.model = model;
        this.data = data;
        this.usage = usage;
    }

    /**
     * 获取模型
     */
    public String getModel() {
        return model;
    }

    /**
     * 获取数据
     */
    public List<Embedding> getData() {
        return data;
    }

    /**
     * 获取使用情况
     */
    public Usage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "{" +
                "model='" + model + '\'' +
                ", data=" + data +
                ", usage=" + usage +
                '}';
    }
}