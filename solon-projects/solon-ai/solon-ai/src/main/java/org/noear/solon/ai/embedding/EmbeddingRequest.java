package org.noear.solon.ai.embedding;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author noear 2025/2/17 created
 */
public interface EmbeddingRequest {
    EmbeddingRequest options(EmbeddingOptions options);

    EmbeddingRequest options(Consumer<EmbeddingOptions> optionsBuilder);

    EmbeddingResponse call() throws IOException;
}

