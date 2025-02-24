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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.noear.snack.ONode;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.QueryCondition;

/**
 * Elasticsearch 矢量存储知识库
 *
 * @author noear
 * @since 3.1
 */
public class ElasticsearchRepository implements RepositoryStorable {
    /**
     * 向量模型，用于将文档内容转换为向量表示
     */
    private final EmbeddingModel embeddingModel;

    /**
     * Elasticsearch 客户端，用于与 ES 服务器交互
     */
    private final RestHighLevelClient client;

    /**
     * ES 索引名称，用于存储文档
     */
    private final String indexName;

    /**
     * 查询转换器，用于将查询条件转换为 ES 查询语句
     */
    private final ElasticsearchQueryTranslator queryTranslator;

    private final BulkProcessor bulkProcessor;

    /**
     * 构造函数
     *
     * @param embeddingModel 向量模型，用于生成文档的向量表示
     * @param client ES客户端
     * @param indexName 索引名称
     * @author 小奶奶花生米
     */
    public ElasticsearchRepository(EmbeddingModel embeddingModel, RestHighLevelClient client, String indexName) {
        this.embeddingModel = embeddingModel;
        this.client = client;
        this.indexName = indexName;
        this.queryTranslator = new ElasticsearchQueryTranslator();

        // 初始化 BulkProcessor
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {}

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {}

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {}
        };

        this.bulkProcessor = BulkProcessor.builder(
                (request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                listener)
                .build();

        // 初始化索引
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            if (!client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
                CreateIndexRequest request = new CreateIndexRequest(indexName);

                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject();
                {
                    builder.startObject("mappings");
                    {
                        builder.startObject("properties");
                        {
                            builder.startObject("content")
                                    .field("type", "text")
                                    .endObject();
                            builder.startObject("metadata")
                                    .field("type", "object")
                                    .endObject();
                            builder.startObject("embedding")
                                    .field("type", "dense_vector")
                                    .field("dims", 3)
                                    .endObject();
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                }
                builder.endObject();

                request.source(builder);
                client.indices().create(request, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Elasticsearch index", e);
        }
    }

    /**
     * 批量存储文档
     * 将文档内容转换为向量并存储到ES中
     *
     * @param documents 要存储的文档列表
     * @throws IOException 如果存储过程中发生IO错误
     * @author 小奶奶花生米
     */
    @Override
    public void store(List<Document> documents) throws IOException {
        for (Document doc : documents) {
            float[] embedding = embeddingModel.embed(doc.getContent());
            String id = java.util.UUID.randomUUID().toString();
            doc.setId(id);
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("content", doc.getContent());
                builder.field("metadata", doc.getMetadata());
                builder.field("embedding", embedding);
            }
            builder.endObject();

            bulkProcessor.add(new IndexRequest(indexName)
                    .id(id)
                    .source(builder));
        }

        // 等待所有请求完成
        bulkProcessor.flush();
    }

    /**
     * 删除指定ID的文档
     *
     * @param id 要删除的文档ID
     * @author 小奶奶花生米
     */
    @Override
    public void remove(String id) {
        try {
            if ("*".equals(id)) {
                // 删除所有文档
                DeleteByQueryRequest request = new DeleteByQueryRequest(indexName)
                        .setQuery(QueryBuilders.matchAllQuery());
                client.deleteByQuery(request, RequestOptions.DEFAULT);
            } else {
                // 删除单个文档
                DeleteRequest request = new DeleteRequest(indexName, id);
                client.delete(request, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete document: " + id, e);
        }
    }

    /**
     * 搜索文档
     * 支持文本搜索、向量相似度搜索和元数据过滤
     *
     * @param condition 查询条件，包含查询文本、过滤器等
     * @return 匹配的文档列表
     * @throws IOException 如果搜索过程中发生IO错误
     * @author 小奶奶花生米
     */
    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        // 转换查询条件
        Map<String, Object> query = queryTranslator.translate(condition);

        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source()
                .query(QueryBuilders.wrapperQuery(ONode.stringify(query)))
                .size(condition.getLimit() > 0 ? condition.getLimit() : 10);  // 设置返回数量

        // 执行搜索
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        // 处理结果
        List<Document> results = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();

            String content = (String) source.get("content");
            Map<String, Object> metadata = (Map<String, Object>) source.get("metadata");

            results.add(new Document(content, metadata));
        }

        return results;
    }
}
