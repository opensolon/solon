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
package org.noear.solon.ai.rag.repository;

import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.QueryCondition;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Elasticsearch 矢量存储知识库
 *
 * @author noear
 * @since 3.1
 */
public class ElasticsearchRepository implements RepositoryStorable {
    private final EmbeddingModel embeddingModel;
    private Object client;
    public ElasticsearchRepository(EmbeddingModel embeddingModel, Object client) {
        this.embeddingModel = embeddingModel;
        this.client = client;
    }

    @Override
    public void store(List<Document> documents) throws IOException {

    }

    @Override
    public void remove(String id) {

    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        return Collections.emptyList();
    }
}
