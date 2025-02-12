package org.noear.solon.ai.rag;

/**
 * @author noear 2025/2/12 created
 */
public class EmbeddingModelDefault implements EmbeddingModel {
    private EmbeddingConfig config;
    public EmbeddingModelDefault(EmbeddingConfig config) {
        this.config = config;
    }
}
