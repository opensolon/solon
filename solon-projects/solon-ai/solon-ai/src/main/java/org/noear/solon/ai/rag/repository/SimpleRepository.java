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

import org.noear.solon.ai.embedding.Embedding;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 简单知识库（基于本地内存实现）
 *
 * @author noear
 * @since 3.1
 */
public class SimpleRepository implements RepositoryStorable {
    private final EmbeddingModel embeddingModel;
    private final Map<String, Document> store = new ConcurrentHashMap<>();

    public SimpleRepository(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void put(List<Document> documents) throws IOException {
        List<String> texts = new ArrayList<>();
        documents.forEach(d -> texts.add(d.getContent()));

        List<Embedding> embeddings = embeddingModel.input(texts).call().getData();

        for (int i = 0; i < embeddings.size(); ++i) {
            Document doc = documents.get(i);
            doc.setEmbedding(embeddings.get(i).getEmbedding());
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
    public List<Document> search(SearchCondition condition) throws IOException {
        float[] userQueryEmbedding = embeddingModel.embed(condition.getQuery());

        return this.store.values().stream()
                .filter(condition.getFilter())
                .map(doc -> mapDo(doc, userQueryEmbedding))
                .filter(doc -> filterDo(doc, condition))
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit((long) condition.getLimit())
                .collect(Collectors.toList());
    }

    private Document mapDo(Document doc, float[] userQueryEmbedding) {
        //方便调试
        return new Document(doc.getId(),
                doc.getContent(),
                doc.getMetadata(),
                SearchUtil.cosineSimilarity(userQueryEmbedding, doc.getEmbedding()));
    }

    private boolean filterDo(Document doc, SearchCondition condition) {
        //方便调试
        return doc.getScore() >= condition.getSimilarityThreshold();
    }
}