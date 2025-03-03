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

import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.*;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.GetReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;

import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.SimilarityUtil;
import org.noear.solon.ai.rag.util.ListUtil;
import org.noear.solon.ai.rag.util.QueryCondition;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Milvus 矢量存储知识库
 *
 * @author linziguan
 * @since 3.1
 */
@Preview("3.1")
public class MilvusRepository implements RepositoryStorable {
    private final EmbeddingModel embeddingModel;
    private final MilvusClientV2 client;
    private final String collectionName;
    private final Gson gson = new Gson();

    public MilvusRepository(EmbeddingModel embeddingModel, MilvusClientV2 client, String collectionName) {
        this.embeddingModel = embeddingModel;
        //客户端的构建由外部完成
        this.client = client;
        //指定集合
        this.collectionName = collectionName;

        initRepository();
    }

    /**
     * 初始化仓库
     */
    public void initRepository() {
        // 查询是否存在
        boolean exists = client.hasCollection(HasCollectionReq.builder()
                .collectionName(collectionName)
                .build());

        if (exists == false) {
            // 构建一个collection，用于存储document
            try {
                CreateCollectionReq.CollectionSchema schema = client.createSchema();
                schema.addField(AddFieldReq.builder()
                        .fieldName("id")
                        .dataType(DataType.VarChar)
                        .maxLength(64)
                        .isPrimaryKey(true)
                        .autoID(false)
                        .build());

                int dim = embeddingModel.dimensions();
                schema.addField(AddFieldReq.builder()
                        .fieldName("embedding")
                        .dataType(DataType.FloatVector)
                        .dimension(dim)
                        .build());
                schema.addField(AddFieldReq.builder()
                        .fieldName("content")
                        .dataType(DataType.VarChar)
                        .maxLength(65535)
                        .build());
                schema.addField(AddFieldReq.builder()
                        .fieldName("metadata")
                        .dataType(DataType.JSON)
                        .build());

                IndexParam indexParamForIdField = IndexParam.builder()
                        .fieldName("id")
                        .build();

                IndexParam indexParamForVectorField = IndexParam.builder()
                        .fieldName("embedding")
                        .indexName("embedding_index")
                        .indexType(IndexParam.IndexType.IVF_FLAT)
                        .metricType(IndexParam.MetricType.COSINE)
                        .extraParams(Collections.singletonMap("nlist", 128))
                        .build();

                List<IndexParam> indexParams = new ArrayList<>();
                indexParams.add(indexParamForIdField);
                indexParams.add(indexParamForVectorField);

                CreateCollectionReq customizedSetupReq1 = CreateCollectionReq.builder()
                        .collectionName(collectionName)
                        .collectionSchema(schema)
                        .indexParams(indexParams)
                        .build();

                client.createCollection(customizedSetupReq1);

                GetLoadStateReq customSetupLoadStateReq1 = GetLoadStateReq.builder()
                        .collectionName(collectionName)
                        .build();

                client.getLoadState(customSetupLoadStateReq1);
            } catch (Exception err) {
                throw new RuntimeException(err);
            }
        }
    }

    /**
     * 注销仓库
     */
    public void dropRepository() {
        client.dropCollection(DropCollectionReq.builder()
                .collectionName(collectionName)
                .build());
    }

    @Override
    public void insert(List<Document> documents) throws IOException {
        if (Utils.isEmpty(documents)) {
            return;
        }

        // 分块处理
        for (List<Document> sub : ListUtil.partition(documents, 20)) {
            // 批量embedding
            embeddingModel.embed(sub);

            // 转换成json存储
            List<JsonObject> docObjs = sub.stream().map(this::toJsonObject)
                    .collect(Collectors.toList());

            InsertReq insertReq = InsertReq.builder()
                    .collectionName(collectionName)
                    .data(docObjs)
                    .build();

            //如果需要更新，请先移除再插入（即不支持更新）
            client.insert(insertReq);
        }
    }

    @Override
    public void delete(String... ids) throws IOException {
        client.delete(DeleteReq.builder()
                .collectionName(collectionName)
                .ids(Arrays.asList(ids))
                .build());
    }

    @Override
    public boolean exists(String id) throws IOException {
        return client.get(GetReq.builder()
                .collectionName(collectionName)
                .ids(Arrays.asList(id))
                .build()).getGetResults().size() > 0;

    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        FloatVec queryVector = new FloatVec(embeddingModel.embed(condition.getQuery()));
        SearchReq searchReq = SearchReq.builder()
                .collectionName(collectionName)
                .data(Collections.singletonList(queryVector))
                .topK(condition.getLimit())
                .outputFields(Arrays.asList("content", "metadata"))
                .build();

        SearchResp searchResp = client.search(searchReq);

        Stream<Document> docs = searchResp.getSearchResults().stream()
                .flatMap(r -> r.stream())
                .map(this::toDocument);

        //再次过滤下
        return SimilarityUtil.filter(condition, docs);
    }

    //文档转为 JsonObject
    private JsonObject toJsonObject(Document doc) {
        if (doc.getId() == null) {
            doc.id(Utils.uuid());
        }

        return gson.toJsonTree(doc).getAsJsonObject();
    }

    /**
     * 搜索结果转为文档
     */
    private Document toDocument(SearchResp.SearchResult result) {
        Map<String, Object> entity = result.getEntity();
        String content = (String) entity.get("content");
        JsonObject metadataJson = (JsonObject) entity.get("metadata");
        Map<String, Object> metadata = null;
        if (metadataJson != null) {
            metadata = gson.fromJson(metadataJson, Map.class);
        }
        float[] embedding = (float[]) entity.get("embedding");

        Document doc = new Document((String) result.getId(), content, metadata, result.getScore());
        return doc.embedding(embedding);
    }
}