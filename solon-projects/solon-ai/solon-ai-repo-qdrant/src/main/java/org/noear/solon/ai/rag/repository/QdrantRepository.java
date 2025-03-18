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

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points.*;

import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.util.SimilarityUtil;
import org.noear.solon.ai.rag.util.ListUtil;
import org.noear.solon.ai.rag.util.QueryCondition;

import com.google.gson.Gson;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.WithPayloadSelectorFactory.include;
import static io.qdrant.client.WithVectorsSelectorFactory.enable;

/**
 * Qdrant 矢量存储知识库
 *
 * @author Anush008
 *
 * @since 3.1.2
 */
@Preview("3.1.2")
public class QdrantRepository implements RepositoryStorable {
    private static final String DEFAULT_CONTENT_KEY = "content";
    private static final String DEFAULT_METADATA_KEY = "metadata";

    private final String contentKey;
    private final String metadataKey;

    private final EmbeddingModel embeddingModel;
    private final QdrantClient client;
    private final String collectionName;
    private final Gson gson = new Gson();

    public QdrantRepository(EmbeddingModel embeddingModel, QdrantClient client, String collectionName) {
        this(embeddingModel, client, collectionName, DEFAULT_CONTENT_KEY, DEFAULT_METADATA_KEY);
    }

    public QdrantRepository(EmbeddingModel embeddingModel, QdrantClient client, String collectionName,
            String contentKey, String metadataKey) {
        this.embeddingModel = embeddingModel;
        this.client = client;
        this.collectionName = collectionName;
        this.contentKey = contentKey;
        this.metadataKey = metadataKey;

        initRepository();
    }

    public void initRepository() {
        try {
            boolean exists = client.collectionExistsAsync(collectionName).get();

            if (!exists) {
                int dimensions = embeddingModel.dimensions();

                client.createCollectionAsync(collectionName,
                        VectorParams.newBuilder().setSize(dimensions).setDistance(Distance.Cosine).build()).get();

            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException("Failed to initialize Qdrant repository", e);
        }
    }

    public void dropRepository() {
        try {
            client.deleteCollectionAsync(collectionName).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to drop Qdrant repository", e);
        }
    }

    @Override
    public void insert(List<Document> documents) throws IOException {
        if (Utils.isEmpty(documents)) {
            return;
        }

        try {
            for (List<Document> sub : ListUtil.partition(documents, 20)) {
                embeddingModel.embed(sub);

                List<PointStruct> points = sub.stream().map(this::toPointStruct).collect(Collectors.toList());

                client.upsertAsync(
                        UpsertPoints.newBuilder().setCollectionName(collectionName).addAllPoints(points).build()).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to insert documents into Qdrant", e);
        }
    }

    @Override
    public void delete(String... ids) throws IOException {
        try {
            List<PointId> pointIds = Arrays.stream(ids).map(id -> PointId.newBuilder().setUuid(id).build())
                    .collect(Collectors.toList());

            client.deleteAsync(collectionName, pointIds).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to delete documents from Qdrant", e);
        }
    }

    @Override
    public boolean exists(String id) throws IOException {
        try {
            List<RetrievedPoint> points = client.retrieveAsync(GetPoints.newBuilder().setCollectionName(collectionName)
                    .addIds(PointIdFactory.id(UUID.fromString(id))).build(), null).get();

            return points.size() > 0;
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to check document existence in Qdrant", e);
        }
    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        try {
            float[] queryVector = embeddingModel.embed(condition.getQuery());

            List<ScoredPoint> points = client.queryAsync(QueryPoints.newBuilder().setCollectionName(collectionName)
                    .setQuery(nearest(queryVector)).setLimit(condition.getLimit())
                    .setScoreThreshold((float) condition.getSimilarityThreshold())
                    .setWithPayload(include(Arrays.asList(contentKey, metadataKey))).setWithVectors(enable(true))
                    .build()).get();

            Stream<Document> docs = points.stream().map(this::toDocument);

            return SimilarityUtil.filter(condition, docs);
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to search documents in Qdrant", e);
        }
    }

    private PointStruct toPointStruct(Document doc) {
        if (doc.getId() == null) {
            doc.id(Utils.uuid());
        }

        Map<String, JsonWithInt.Value> payload = new HashMap<>();

        payload.put(contentKey, value(doc.getContent()));

        if (doc.getMetadata() != null) {
            String metadataJson = gson.toJson(doc.getMetadata());
            payload.put("metadata", value(metadataJson));
        }

        return PointStruct.newBuilder().setId(PointIdFactory.id(UUID.fromString(doc.getId())))
                .setVectors(vectors(doc.getEmbedding())).putAllPayload(payload).build();
    }

    @SuppressWarnings("unchecked")
    private Document toDocument(ScoredPoint scoredPoint) {
        String id = scoredPoint.getId().getUuid();
        float score = scoredPoint.getScore();

        Map<String, JsonWithInt.Value> payload = scoredPoint.getPayloadMap();

        String content = payload.get(contentKey).getStringValue();

        Map<String, Object> metadata = null;
        if (payload.containsKey(metadataKey)) {
            String metadataJson = payload.get(metadataKey).getStringValue();
            metadata = gson.fromJson(metadataJson, Map.class);
        }

        float[] embedding = listToFloatArray(scoredPoint.getVectors().getVector().getDataList());

        Document doc = new Document(id, content, metadata, score);
        return doc.embedding(embedding);
    }

    private float[] listToFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
