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

import io.github.javpower.vectorexclient.VectorRexClient;
import io.github.javpower.vectorexclient.builder.QueryBuilder;
import io.github.javpower.vectorexclient.req.CollectionDataAddReq;
import io.github.javpower.vectorexclient.req.CollectionDataDelReq;
import io.github.javpower.vectorexclient.res.ServerResponse;
import io.github.javpower.vectorexclient.res.VectorSearchResult;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.QueryCondition;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vectorex 矢量存储知识库
 * @author noear
 * @since 3.1
 */
public class VectorexRepository implements RepositoryStorable {
    private final EmbeddingModel embeddingModel;
    private final VectorRexClient client;
    private final String collectionName;

    public VectorexRepository(EmbeddingModel embeddingModel,
                              VectorRexClient client,
                              String collectionName) {
        this.embeddingModel = embeddingModel;
        //客户端的构建由外部完成
        this.client = client;
        this.collectionName = collectionName;
    }

    @Override
    public void put(List<Document> documents) throws IOException {
        embeddingModel.embed(documents);

        for (Document doc : documents) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", doc.getId());
            data.put("content", doc.getContent());
            data.put("embedding", doc.getEmbedding());
            data.put("metadata", doc.getMetadata());

            CollectionDataAddReq req = new CollectionDataAddReq();
            req.setCollectionName(collectionName);
            req.setMetadata(data);
            client.addCollectionData(req);
        }
    }

    @Override
    public void remove(String id) {
        CollectionDataDelReq req = new CollectionDataDelReq();
        req.setCollectionName(collectionName);
        req.setId(id);
        client.deleteCollectionData(req);
    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        float[] queryEmbedding = embeddingModel.embed(condition.getQuery());

        QueryBuilder req = new QueryBuilder(collectionName);
        req.vector("embedding", Arrays.asList(queryEmbedding));
        req.setTopK(condition.getLimit());

        ServerResponse<List<VectorSearchResult>> resp = client.queryCollectionData(req);

        if (resp.isSuccess() == false) {
            throw new IOException(resp.getMsg());
        } else {
            return resp.getData().stream()
                    .map(v1 -> new Document(
                            v1.getId(),
                            (String) v1.getData().getMetadata().get("content"),
                            (Map<String, Object>) v1.getData().getMetadata().get("metadata"),
                            v1.getScore()))
                    .collect(Collectors.toList());
        }
    }
}