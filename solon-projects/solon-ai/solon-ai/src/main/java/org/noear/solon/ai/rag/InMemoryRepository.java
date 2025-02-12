package org.noear.solon.ai.rag;

import java.util.List;

/**
 * @author noear 2025/2/12 created
 */
public class InMemoryRepository implements Repository {

    public InMemoryRepository(EmbeddingModel embeddings) {

    }

    @Override
    public void addDocuments(List<Document> documents) {

    }

    @Override
    public Object retrieve(String question) {
        return null;
    }
}
