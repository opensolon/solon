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

import java.time.Duration;

/**
 * 聊天模型构建器实现
 *
 * @author noear
 * @since 3.1
 */
public class EmbeddingModelBuilder {
    private final EmbeddingConfig config;

    public EmbeddingModelBuilder(String apiUrl) {
        this.config = new EmbeddingConfig();
        this.config.apiUrl = apiUrl;
    }

    public EmbeddingModelBuilder(EmbeddingConfig config) {
        this.config = config;
    }

    public EmbeddingModelBuilder apiKey(String apiKey) {
        config.apiKey = apiKey;
        return this;
    }

    public EmbeddingModelBuilder provider(String provider) {
        config.provider = provider;
        return this;
    }

    public EmbeddingModelBuilder model(String model) {
        config.model = model;
        return this;
    }

    public EmbeddingModelBuilder headerSet(String key, String value) {
        config.headers.put(key, value);
        return this;
    }

    public EmbeddingModelBuilder timeout(Duration timeout) {
        if (timeout != null) {
            config.timeout = timeout;
        }

        return this;
    }

    public EmbeddingModel build() {
        return new EmbeddingModelDefault(config);
    }
}