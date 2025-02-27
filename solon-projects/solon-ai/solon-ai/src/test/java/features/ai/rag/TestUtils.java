package features.ai.rag;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.repository.WebSearchRepository;

/**
 * @author noear 2025/2/19 created
 */
public class TestUtils {
    public static ChatModel getChatModelOfGiteeai() {
        final String apiUrl = "https://ai.gitee.com/v1/chat/completions";
        final String apkKey = "PE6JVMP7UQI81GY6AZ0J8WEWWLFHWHROG15XUP18";
        final String model = "Qwen2.5-72B-Instruct";//"DeepSeek-V3"; //deepseek-reasoner//deepseek-chat

        return ChatModel.of(apiUrl).apiKey(apkKey).model(model).build(); //4.初始化语言模型
    }

    public static WebSearchRepository getWebSearchRepositoryOfBochaai() {
        String apiUrl = "https://api.bochaai.com/v1/web-search";
        String apiKey = "sk-5d36eae2c4a54e2596c7625d9888a9d8";

        return WebSearchRepository.of(apiUrl).apiKey(apiKey).build();
    }

    public static EmbeddingModel getEmbeddingModelOfOllama() {
        final String apiUrl = "http://127.0.0.1:11434/api/embed";
        final String provider = "ollama";
        final String model = "bge-m3";//

        return EmbeddingModel.of(apiUrl).provider(provider).model(model).build();
    }
}
