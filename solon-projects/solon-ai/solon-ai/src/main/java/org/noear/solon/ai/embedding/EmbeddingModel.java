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

import org.noear.solon.ai.AiModel;
import org.noear.solon.ai.embedding.dialect.EmbeddingDialect;
import org.noear.solon.ai.embedding.dialect.EmbeddingDialectManager;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 嵌入模型（相当于翻译器）
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class EmbeddingModel implements AiModel {
    private final EmbeddingConfig config;
    private final EmbeddingDialect dialect;

    protected EmbeddingModel(EmbeddingConfig config) {
        this.dialect = EmbeddingDialectManager.select(config);
        this.config = config;
    }

    /**
     * 快捷嵌入
     */
    public float[] embed(String text) throws IOException {
        EmbeddingResponse resp = input(text).call();
        if (resp.getError() != null) {
            throw resp.getError();
        }

        return resp.getData().get(0).getEmbedding();
    }

    /**
     * 维度
     */
    public int dimensions() throws IOException {
        return embed("test").length;
    }

    /**
     * 快捷嵌入
     */
    public void embed(List<Document> documents) throws IOException {
        List<String> texts = new ArrayList<>();
        documents.forEach(d -> texts.add(d.getContent()));

        EmbeddingResponse resp = input(texts).call();
        if (resp.getError() != null) {
            throw resp.getError();
        }

        List<Embedding> embeddings = resp.getData();

        for (int i = 0; i < embeddings.size(); ++i) {
            Document doc = documents.get(i);
            doc.embedding(embeddings.get(i).getEmbedding());
        }
    }

    /**
     * 输入
     */
    public EmbeddingRequest input(String... input) {
        return input(Arrays.asList(input));
    }

    /**
     * 输入
     */
    public EmbeddingRequest input(List<String> input) {
        return new EmbeddingRequest(config, dialect, input);
    }


    /**
     * 构建
     */
    public static Builder of(EmbeddingConfig config) {
        return new Builder(config);
    }

    /**
     * 构建
     */
    public static Builder of(String apiUrl) {
        return new Builder(apiUrl);
    }

    /// /////////////

    /**
     * 嵌入模型构建器实现
     *
     * @author noear
     * @since 3.1
     */
    public static class Builder {
        private final EmbeddingConfig config;

        public Builder(String apiUrl) {
            this.config = new EmbeddingConfig();
            this.config.setApiUrl(apiUrl);
        }

        public Builder(EmbeddingConfig config) {
            this.config = config;
        }

        public Builder apiKey(String apiKey) {
            config.setApiKey(apiKey);
            return this;
        }

        public Builder provider(String provider) {
            config.setProvider(provider);
            return this;
        }

        public Builder model(String model) {
            config.setModel(model);
            return this;
        }

        public Builder headerSet(String key, String value) {
            config.setHeader(key, value);
            return this;
        }

        public Builder timeout(Duration timeout) {
            config.setTimeout(timeout);
            return this;
        }

        public EmbeddingModel build() {
            return new EmbeddingModel(config);
        }
    }
}