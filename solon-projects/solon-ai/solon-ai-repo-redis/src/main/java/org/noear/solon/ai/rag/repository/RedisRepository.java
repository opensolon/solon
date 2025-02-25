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
import org.noear.solon.lang.Preview;
import redis.clients.jedis.PipelineBase;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.RediSearchUtil;
import redis.clients.jedis.search.SearchResult;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 矢量存储知识库 //基于 redis search 乱配
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class RedisRepository implements RepositoryStorable {
    private final EmbeddingModel embeddingModel;
    private final UnifiedJedis client;
    private final String keyPrefix;
    private final String indexName;

    public RedisRepository(EmbeddingModel embeddingModel, UnifiedJedis client, String indexName, String keyPrefix) {
        this.embeddingModel = embeddingModel;
        this.client = client;
        this.indexName = indexName;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void store(List<Document> documents) throws IOException {
        embeddingModel.embed(documents);

        try (PipelineBase pipeline = client.pipelined()) {
            for (Document doc : documents) {
                HashMap<String, Object> setFields = new HashMap(); //设置字段
                setFields.put("embedding", doc.getEmbedding());
                setFields.put("content", doc.getContent());
                setFields.put("metadata", doc.getMetadata());

                pipeline.jsonSetWithEscape(keyPrefix + doc.getId(), setFields);
            }

            pipeline.sync();
        }
    }

    @Override
    public void remove(String id) {
        client.jsonDel(keyPrefix + id);
    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        String queryString = String.format("*=>[KNN %s @%s $%s AS %s]", condition.getLimit(), "embedding", "BLOB", "vector_score");

        List<String> getFields = new ArrayList(); //获取字段
        //getFields.add("embedding");
        getFields.add("content");
        getFields.add("metadata");
        getFields.add("vector_score");

        float[] embedding = this.embeddingModel.embed(condition.getQuery());

        Query query = (new Query(queryString))
                .addParam("BLOB", RediSearchUtil.toByteArray(embedding))
                .returnFields(getFields.toArray(new String[0]))
                .setSortBy("vector_score", true)
                .limit(0, condition.getLimit())
                .dialect(2); //向量搜索

        SearchResult result = client.ftSearch(this.indexName, query);
        return result.getDocuments()
                .stream()
                .filter((d) -> (double) this.similarityScore(d) >= condition.getSimilarityThreshold())
                .map(this::toDocument)
                .collect(Collectors.toList());
    }

    private Document toDocument(redis.clients.jedis.search.Document jDoc) {
        String id = jDoc.getId().substring(this.keyPrefix.length());
        String content = jDoc.getString("content");
        Map<String, Object> metadata = (Map) jDoc.get("metadata");
        float score = 1.0F - this.similarityScore(jDoc);

        return new Document(id, content, metadata, score);
    }

    private float similarityScore(redis.clients.jedis.search.Document doc) {
        return (2.0F - Float.parseFloat(doc.getString("vector_score"))) / 2.0F;
    }
}
