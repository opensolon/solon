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
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import io.milvus.v2.service.collection.request.GetLoadStateReq;
import io.milvus.v2.service.collection.request.LoadCollectionReq;
import io.milvus.v2.service.collection.response.ListCollectionsResp;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.UpsertReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import io.milvus.v2.service.vector.response.UpsertResp;

import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.ListUtil;
import org.noear.solon.ai.rag.util.QueryCondition;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public MilvusRepository(EmbeddingModel embeddingModel, MilvusClientV2 client, String collectionName) {
        this.embeddingModel = embeddingModel;
        //客户端的构建由外部完成
        this.client = client;
        //指定集合
        this.collectionName = collectionName;

        buildCollection();
    }
    
    public void buildCollection() {
    	// 查询是否存在
        ListCollectionsResp listCollectionsResp = client.listCollections();

        if(!listCollectionsResp.getCollectionNames().contains(collectionName)) {
        	// 构建一个collection，用于存储document
        	CreateCollectionReq.CollectionSchema schema = client.createSchema();
        	schema.addField(AddFieldReq.builder()
        	        .fieldName("id")
        	        .dataType(DataType.VarChar)
        	        .maxLength(64)
        	        .isPrimaryKey(true)
        	        .autoID(false)
        	        .build());
        	schema.addField(AddFieldReq.builder()
        	        .fieldName("embedding")
        	        .dataType(DataType.FloatVector)
        	        .dimension(1024)
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
//        	        .indexType(IndexParam.IndexType.STL_SORT)
        	        .build();
        	
        	IndexParam indexParamForVectorField = IndexParam.builder()
        		    .fieldName("embedding")
        		    .indexName("embedding_index")
        		    .indexType(IndexParam.IndexType.IVF_FLAT)
        		    .metricType(IndexParam.MetricType.COSINE)
        		    .extraParams(new HashMap<String,Object>(1) {
        		    	{
        		    		put("nlist", 128);
        		    	}
        		    })
        		    .build();
        	
        	List<IndexParam> indexParams = new ArrayList<>();
        	indexParams.add(indexParamForIdField);
        	indexParams.add(indexParamForVectorField);
        	
        	CreateCollectionReq customizedSetupReq1 = CreateCollectionReq.builder()
        	        .collectionName(collectionName)
        	        .collectionSchema(schema)
        	        .indexParams(indexParams)
//        	        .enableDynamicField(true)
        	        .build();
        	
        	client.createCollection(customizedSetupReq1);

        	GetLoadStateReq customSetupLoadStateReq1 = GetLoadStateReq.builder()
        	        .collectionName(collectionName)
        	        .build();
        	
        	Boolean loaded = client.getLoadState(customSetupLoadStateReq1);
        	
        }
    }
    
    public void dropCollection() {
    	ListCollectionsResp listCollectionsResp = client.listCollections();
        if(listCollectionsResp.getCollectionNames().contains(collectionName)) {
        	DropCollectionReq dropQuickSetupParam = DropCollectionReq.builder()
        	        .collectionName(collectionName)
        	        .build();

        	client.dropCollection(dropQuickSetupParam);
        }
    	
    }

    @Override
    public void store(List<Document> documents) throws IOException {
        if(Utils.isEmpty(documents)) {
            return;
        }

    	// 批量embedding
        for (List<Document> sub : ListUtil.partition(documents, 20)) {
            embeddingModel.embed(sub);
        }
    	
    	// 转换成json存储
    	Gson gson = new Gson();
    	List<JsonObject> insertObjs = new ArrayList<>();
    	List<JsonObject> updateObjs = new ArrayList<>();
    	for(Document doc:documents) {
    		if(doc.getEmbedding()==null) {
    			continue;
    		}

    		if(doc.getId()==null) {
    			doc.id(Utils.guid());
    			JsonObject jsonObj = gson.fromJson(gson.toJson(doc),JsonObject.class);
    			jsonObj.remove("score");
    			insertObjs.add(jsonObj);
    		}else {
    			JsonObject jsonObj = gson.fromJson(gson.toJson(doc),JsonObject.class);
    			jsonObj.remove("score");
    			updateObjs.add(jsonObj);
    		}
    	}
    	
    	if(insertObjs.size()>0) {
    		int batchSize = 25;
    		int totalSize = insertObjs.size();
            int numBatches = (totalSize + batchSize - 1) / batchSize; // 计算总批次数
            for (int i = 0; i < numBatches; i++) {
            	int start = i * batchSize;
                int end = Math.min(start + batchSize, totalSize); // 确保最后一组不会越界

                List<JsonObject> batch = insertObjs.subList(start, end);
                
                InsertReq insertReq = InsertReq.builder().collectionName(collectionName).data(batch).build();
        		InsertResp insertResp = client.insert(insertReq);
            }
    	}
    	
		
		if(updateObjs.size()>0) {
			int batchSize = 25;
    		int totalSize = updateObjs.size();
            int numBatches = (totalSize + batchSize - 1) / batchSize; // 计算总批次数
            for (int i = 0; i < numBatches; i++) {
            	int start = i * batchSize;
                int end = Math.min(start + batchSize, totalSize); // 确保最后一组不会越界

                List<JsonObject> batch = updateObjs.subList(start, end);
                
                UpsertReq upsertReq = UpsertReq.builder()
    			        .collectionName(collectionName)
    			        .data(batch)
    			        .build();

    			UpsertResp upsertResp = client.upsert(upsertReq);
            }
			
		}
		
    }

    @Override
    public void remove(String id) {
    	client.delete(DeleteReq.builder()
    	        .collectionName(collectionName)
    	        .ids(Arrays.asList(id))
    	        .build());
    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
    	// Load the collection
    	LoadCollectionReq loadCollectionReq = LoadCollectionReq.builder()
    	        .collectionName(collectionName)
    	        .build();

    	client.loadCollection(loadCollectionReq);

    	// Get load state of the collection
    	GetLoadStateReq loadStateReq = GetLoadStateReq.builder()
    	        .collectionName(collectionName)
    	        .build();

    	Boolean res = client.getLoadState(loadStateReq);
    	if(!res) {
    		return null;
    	}
    	
    	FloatVec queryVector = new FloatVec(embeddingModel.embed(condition.getQuery()));
    	SearchReq searchReq = SearchReq.builder()
    	        .collectionName(collectionName)
    	        .data(Collections.singletonList(queryVector))
    	        
    	        .topK(condition.getLimit())
    	        .outputFields(Arrays.asList("content","metadata"))
    	        .build();
    	
    	SearchResp searchResp = client.search(searchReq);
    	
    	List<Document> list = new ArrayList<>();
    	Document doc = null;
    	Gson gson = new Gson();
    	List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
    	for (List<SearchResp.SearchResult> results : searchResults) {
    	    for (SearchResp.SearchResult result : results) {
    	    	Map<String, Object> entity = result.getEntity();
    	    	String content = (String) entity.get("content");
    	    	JsonObject metadataJson = (JsonObject) entity.get("metadata");
    	    	Map<String, Object> metadata = null;
    	    	if(metadataJson!=null) {
    	    		metadata = gson.fromJson(metadataJson, Map.class);
    	    	}
    	    	float[] embedding = (float[]) entity.get("embedding");
    	    	
    	    	doc = new Document((String)result.getId(),content,metadata,result.getScore());
    	    	doc.embedding(embedding);
    	    	if(condition.getFilter().test(doc)) {
    	    		list.add(doc);
    	    	}
    	    }
    	}
        return list;
    }
}