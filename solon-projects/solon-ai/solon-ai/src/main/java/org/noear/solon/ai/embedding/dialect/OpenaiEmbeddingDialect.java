package org.noear.solon.ai.embedding.dialect;

import org.noear.solon.ai.embedding.EmbeddingConfig;

/**
 * @author noear
 * @since 3.1
 */
public class OpenaiEmbeddingDialect extends AbstractEmbeddingDialect {
    private static OpenaiEmbeddingDialect instance = new OpenaiEmbeddingDialect();

    public static OpenaiEmbeddingDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(EmbeddingConfig config) {
        return false;
    }
}
