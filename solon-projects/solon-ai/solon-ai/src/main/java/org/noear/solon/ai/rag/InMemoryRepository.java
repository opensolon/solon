package org.noear.solon.ai.rag;

import org.noear.solon.ai.rag.loader.DocumentLoader;

import java.util.List;

/**
 * @author noear 2025/2/12 created
 */
public class InMemoryRepository implements Repository {

    public InMemoryRepository(EmbeddingModel embeddings) {

    }

    @Override
    public void load(DocumentLoader loader) {

    }

    @Override
    public State retrieve(String question) {
        return null;
    }
}
