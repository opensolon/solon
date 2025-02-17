package org.noear.solon.ai.embedding.dialect;

import org.noear.solon.ai.embedding.EmbeddingConfig;

/**
 * @author noear
 * @since 3.1
 */
public class OllamaEmbeddingDialect extends AbstractEmbeddingDialect {
    private static OllamaEmbeddingDialect instance = new OllamaEmbeddingDialect();

    public static OllamaEmbeddingDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(EmbeddingConfig config) {
        return "ollama".equals(config.provider());
    }
}
