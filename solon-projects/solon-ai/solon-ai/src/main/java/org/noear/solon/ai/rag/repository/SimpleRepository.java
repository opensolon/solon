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
 * @author noear
 * @since 3.1
 */
public class SimpleRepository implements Repository {
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
    public List<Document> search(SearchRequest request) throws IOException {
        float[] userQueryEmbedding = embeddingModel.embed(request.getQuery());

        return this.store.values().stream()
                .map(doc -> mapDo(doc, userQueryEmbedding))
                .filter(doc -> filterDo(doc, request))
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit((long) request.getTopK())
                .collect(Collectors.toList());
    }

    private Document mapDo(Document doc, float[] userQueryEmbedding) {
        //方便调试
        return new Document(doc.getId(),
                doc.getContent(),
                doc.getMetadata(),
                EmbedMath.cosineSimilarity(userQueryEmbedding, doc.getEmbedding()));
    }

    private boolean filterDo(Document doc, SearchRequest request) {
        //方便调试
        return doc.getScore() >= request.getSimilarityThreshold();
    }
}
