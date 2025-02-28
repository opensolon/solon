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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.QueryCondition;
import org.noear.solon.ai.rag.util.SimilarityUtil;
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

        buildIndex();
    }

    /**
     * 初始化向量索引
     */
    public void buildIndex() {
        try {
            // 检查并初始化索引
            client.ftInfo(indexName);
        } catch (Exception e) {
            // 索引不存在时才创建

            try {
                int dim = embeddingModel.embed("test").length;

                // 配置向量索引参数
                Map<String, Object> vectorArgs = new HashMap<>();
                vectorArgs.put("TYPE", VECTOR_TYPE);
                vectorArgs.put("DIM", String.valueOf(dim));
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
            } catch (Exception err) {
                throw new RuntimeException(err);
            }
        }
    }

    public void dropIndex() {
        client.ftDropIndex(indexName);
    }

    /**
     * 存储文档列表
     *
     * @param documents 待存储的文档列表
     * @throws IOException 如果存储过程中发生 IO 错误
     */
    @Override
    public void store(List<Document> documents) throws IOException {
        if (Utils.isEmpty(documents)) {
            return;
        }

        // 批量处理，每批20个
        int batchSize = 20;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            List<Document> batch = documents.subList(i, end);

            embeddingModel.embed(batch);

            PipelineBase pipeline = null;
            try {
                pipeline = client.pipelined();
                for (Document doc : batch) {
                    if (doc.getId() == null) {
                        doc.id(Utils.uuid());
                    }

                    String key = keyPrefix + doc.getId();
                    float[] embedding = doc.getEmbedding();

                    // 将向量转换为字节数组
                    byte[] bytes = new byte[embedding.length * 4];
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    for (float f : embedding) {
                        buffer.putFloat(f);
                    }

                    String metadataJson = ONode.stringify(doc.getMetadata());

                    // 存储文档字段
                    Map<byte[], byte[]> fields = new HashMap<>(16);
                    fields.put("content".getBytes(StandardCharsets.UTF_8), doc.getContent().getBytes(StandardCharsets.UTF_8));
                    fields.put("embedding".getBytes(StandardCharsets.UTF_8), bytes);
                    fields.put("metadata".getBytes(StandardCharsets.UTF_8), metadataJson.getBytes(StandardCharsets.UTF_8));

                    pipeline.hmset(key.getBytes(StandardCharsets.UTF_8), fields);
                }
                pipeline.sync();
            } finally {
                if (pipeline != null) {
                    pipeline.close();
                }
            }
        }
    }

    /**
     * 删除指定 ID 的文档
     *
     * @param ids 文档 ID
     */
    @Override
    public void remove(String... ids) {
        PipelineBase pipeline = null;
        try {
            pipeline = client.pipelined();
            for (String id : ids) {
                pipeline.del(keyPrefix + id);
            }
            pipeline.sync();
        } finally {
            if (pipeline != null) {
                pipeline.close();
            }
        }
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
        // 生成查询向量
        float[] queryEmbedding = embeddingModel.embed(condition.getQuery());

        // 将向量转换为字节数组
        byte[] bytes = new byte[queryEmbedding.length * 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float f : queryEmbedding) {
            buffer.putFloat(f);
        }

        // 构建混合查询（文本 + 向量）
        StringBuilder queryBuilder = new StringBuilder();
        
        // 添加文本过滤
        if (!Utils.isEmpty(condition.getQuery())) {
            queryBuilder.append("(@content:\"").append(condition.getQuery()).append("\") => ");
        }
        
        // 添加向量搜索
        queryBuilder.append("[KNN ").append(condition.getLimit())
                   .append(" @embedding $BLOB AS vector_score]");

        Query query = new Query(queryBuilder.toString())
                .addParam("BLOB", bytes)
                .returnFields("content", "metadata", "vector_score")
                .setSortBy("vector_score", false)
                .limit(0, condition.getLimit())
                .dialect(2);

        SearchResult result = client.ftSearch(indexName, query);

        return SimilarityUtil.filter(condition, result.getDocuments()
                .stream()
                .map(this::toDocument));
    }

    private Document toDocument(redis.clients.jedis.search.Document jDoc) {
        String id = jDoc.getId().substring(keyPrefix.length());
        String content = jDoc.getString("content");
        Map<String, Object> metadata = ONode.deserialize(jDoc.getString("metadata"), Map.class);

        return new Document(id, content, metadata, jDoc.getScore());
    }
}
