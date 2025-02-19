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
import org.noear.solon.lang.Preview;

import java.io.IOException;
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
     * 快捷内嵌
     */
    public float[] embed(String text) throws IOException {
        return input(text).call().getData().get(0).getEmbedding();
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
    public static EmbeddingModelBuilder of(EmbeddingConfig config) {
        return new EmbeddingModelBuilder(config);
    }

    /**
     * 构建
     */
    public static EmbeddingModelBuilder of(String apiUrl) {
        return new EmbeddingModelBuilder(apiUrl);
    }
}