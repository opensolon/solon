package features.ai.rag;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.repository.WebSearchRepository;

/**
 * @author noear 2025/2/19 created
 */
public class TestUtils {
    public static ChatModel getChatModel() {
        final String apiUrl = "http://127.0.0.1:11434/api/chat";
        final String provider = "ollama";
        final String model = "llama3.2";//"DeepSeek-V3"; //deepseek-reasoner//deepseek-chat

        return ChatModel.of(apiUrl).provider(provider).model(model).build(); //4.初始化语言模型
    }

    public static WebSearchRepository getWebSearchRepository() {
        String apiUrl = "https://api.bochaai.com/v1/web-search";
        String apiKey = "sk-5d36eae2c4a54e2596c7625d9888a9d8";

        return WebSearchRepository.of(apiUrl).apiKey(apiKey).build();
    }

    public static EmbeddingModel getEmbeddingModel() {
        final String apiUrl = "http://127.0.0.1:11434/api/embed";
        final String provider = "ollama";
        final String model = "bge-m3";//

        return EmbeddingModel.of(apiUrl).provider(provider).model(model).build();
    }
}
