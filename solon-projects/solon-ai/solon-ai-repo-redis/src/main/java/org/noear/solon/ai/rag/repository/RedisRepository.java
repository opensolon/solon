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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.ListUtil;
import org.noear.solon.ai.rag.util.QueryCondition;
import org.noear.solon.lang.Preview;

import redis.clients.jedis.PipelineBase;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TagField;
import redis.clients.jedis.search.schemafields.TextField;
import redis.clients.jedis.search.schemafields.VectorField;

/**
 * Redis 矢量存储知识库，基于 Redis Search 实现
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class RedisRepository implements RepositoryStorable {
    /**
     * 向量数据类型
     */
    private static final String VECTOR_TYPE = "FLOAT32";
    /**
     * 向量距离度量方式
     */
    private static final String DISTANCE_METRIC = "COSINE";
    /**
     * 向量维度
     */
    private static final int VECTOR_DIM = 3;
    /**
     * 初始容量
     */
    private static final int INITIAL_CAP = 100;
    /**
     * HNSW 图的每层最大节点数
     */
    private static final int M = 16;
    /**
     * HNSW 构建时的候选集大小
     */
    private static final int EF_CONSTRUCTION = 200;

    /**
     * 嵌入模型，用于生成文档的向量表示
     */
    private final EmbeddingModel embeddingModel;
    /**
     * Redis 客户端
     */
    private final UnifiedJedis client;
    /**
     * 索引名称
     */
    private final String indexName;
    /**
     * 键前缀
     */
    private final String keyPrefix;

    /**
     * 创建 Redis 知识库
     *
     * @param embeddingModel 嵌入模型
     * @param client         Redis 客户端
     */
    public RedisRepository(EmbeddingModel embeddingModel, UnifiedJedis client) {
        this(embeddingModel, client, "idx:solon-ai", "doc:");
    }

    /**
     * 创建 Redis 知识库
     *
     * @param embeddingModel 嵌入模型
     * @param client         Redis 客户端
     * @param indexName      索引名称
     * @param keyPrefix      键前缀
     */
    public RedisRepository(EmbeddingModel embeddingModel, UnifiedJedis client, String indexName, String keyPrefix) {
        this.embeddingModel = embeddingModel;
        this.client = client;
        this.indexName = indexName;
        this.keyPrefix = keyPrefix;

        // 检查并初始化索引
        try {
            client.ftInfo(indexName);
        } catch (Exception e) {
            // 索引不存在时才创建
            initializeIndex();
        }
    }

    /**
     * 初始化向量索引
     */
    private void initializeIndex() {
        // 配置向量索引参数
        Map<String, Object> vectorArgs = new HashMap<>();
        vectorArgs.put("TYPE", VECTOR_TYPE);
        vectorArgs.put("DIM", String.valueOf(VECTOR_DIM));
        vectorArgs.put("DISTANCE_METRIC", DISTANCE_METRIC);
        vectorArgs.put("INITIAL_CAP", String.valueOf(INITIAL_CAP));
        vectorArgs.put("M", String.valueOf(M));
        vectorArgs.put("EF_CONSTRUCTION", String.valueOf(EF_CONSTRUCTION));

        // 定义索引字段
        SchemaField[] fields = new SchemaField[]{
                new TextField("content"),  // 文本内容字段
                new VectorField("embedding", VectorField.VectorAlgorithm.HNSW, vectorArgs),  // 向量字段
                new TagField("metadata")  // 元数据字段
        };

        // 创建索引
        client.ftCreate(indexName,
                FTCreateParams.createParams()
                        .on(IndexDataType.HASH)
                        .prefix(keyPrefix),
                fields);
    }

    /**
     * 存储文档列表
     *
     * @param documents 待存储的文档列表
     * @throws IOException 如果存储过程中发生 IO 错误
     */
    @Override
    public void store(List<Document> documents) throws IOException {
        if(Utils.isEmpty(documents)) {
            return;
        }

        // 批量embedding
        for (List<Document> sub : ListUtil.partition(documents, 20)) {
            embeddingModel.embed(sub);
        }

        PipelineBase pipeline = null;
        try {
            pipeline = client.pipelined();
            for (Document doc : documents) {
                if (doc.getId() == null) {
                    doc.id(generateId());
                }

                String key = keyPrefix + doc.getId();
                float[] embedding = doc.getEmbedding();

                // 将向量转换为字节数组
                byte[] bytes = new byte[VECTOR_DIM * 4];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                for (float f : embedding) {
                    buffer.putFloat(f);
                }

                // 存储文档字段
                Map<byte[], byte[]> fields = new HashMap<>(16);
                fields.put("content".getBytes(StandardCharsets.UTF_8), doc.getContent().getBytes(StandardCharsets.UTF_8));
                fields.put("embedding".getBytes(StandardCharsets.UTF_8), bytes);
                fields.put("metadata".getBytes(StandardCharsets.UTF_8), "{}".getBytes(StandardCharsets.UTF_8));

                pipeline.hmset(key.getBytes(StandardCharsets.UTF_8), fields);
            }
            pipeline.sync();
        } finally {
            if (pipeline != null) {
                pipeline.close();
            }
        }
    }

    /**
     * 删除指定 ID 的文档
     *
     * @param id 文档 ID
     */
    @Override
    public void remove(String id) {
        client.del(keyPrefix + id);
    }

    /**
     * 搜索文档
     *
     * @param condition 搜索条件
     * @return 匹配的文档列表
     * @throws IOException 如果搜索过程中发生 IO 错误
     */
    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        Query query = new Query(String.format("@content:%s", condition.getQuery()))
                .returnFields("content", "metadata")
                .limit(0, condition.getLimit())
                .dialect(2);

        SearchResult result = client.ftSearch(indexName, query);

        List<Document> documents = new ArrayList<>();
        for (redis.clients.jedis.search.Document doc : result.getDocuments()) {
            String id = doc.getId().substring(keyPrefix.length());
            String content = doc.getString("content");
            documents.add(new Document(id, content, new HashMap<>(16), 1.0f));
        }
        return documents;
    }

    /**
     * 生成唯一文档 ID
     *
     * @return UUID 字符串
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
