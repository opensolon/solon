package features.ai.repo.redis;

import org.noear.solon.ai.embedding.EmbeddingModel;

/**
 * @author noear 2025/2/19 created
 */
public class TestUtils {
    public static EmbeddingModel getEmbeddingModel() {
        final String apiUrl = "http://127.0.0.1:11434/api/embed";
        final String provider = "ollama";
        final String model = "bge-m3";//

        return EmbeddingModel.of(apiUrl).provider(provider).model(model).build();
    }
}
