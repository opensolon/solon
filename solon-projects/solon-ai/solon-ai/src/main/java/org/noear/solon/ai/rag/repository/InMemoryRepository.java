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
import org.noear.solon.ai.rag.util.FilterUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单知识库（基于本地内存实现）
 *
 * @author noear
 * @since 3.1
 */
public class InMemoryRepository implements RepositoryStorable {
    private final EmbeddingModel embeddingModel;
    private final Map<String, Document> store = new ConcurrentHashMap<>();

    public InMemoryRepository(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void put(List<Document> documents) throws IOException {
        embeddingModel.embed(documents);

        for (Document doc : documents) {
            store.put(doc.getId(), doc);
        }
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }

    @Override
    public Document get(String id) {
        return store.get(id);
    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        return FilterUtil.similarityFilter(condition, embeddingModel, store.values());
    }
}