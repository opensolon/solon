package org.noear.solon.ai.rag.repository;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.repository.WebSearchRepository;

/**
 * @author noear 2025/2/19 created
 */
public class TestUtils {
    public static ChatModel getChatModelOfGiteeai() {
        final String apiUrl = "https://ai.gitee.com/v1/chat/completions";
        final String apkKey = "JQC6M0GTNPGSCEXZOBUGUX0HVHCOLDIMN6XOSSSA";
        final String model = "Qwen2.5-72B-Instruct";//"DeepSeek-V3"; //deepseek-reasoner//deepseek-chat

        return ChatModel.of(apiUrl).apiKey(apkKey).model(model).build(); //4.初始化语言模型
    }

    public static WebSearchRepository getWebSearchRepositoryOfBochaai() {
        String apiUrl = "https://api.bochaai.com/v1/web-search";
        String apiKey = "sk-5d36eae2c4a54e2596c7625d9888a9d8";

        return WebSearchRepository.of(apiUrl).apiKey(apiKey).build();
    }

    public static EmbeddingModel getEmbeddingModelOfGiteeai() {
        final String apiUrl = "https://ai.gitee.com/v1/embeddings";
        final String apkKey = "JQC6M0GTNPGSCEXZOBUGUX0HVHCOLDIMN6XOSSSA";
        final String provider = "giteeai";
        final String model = "bge-m3";//

        return EmbeddingModel.of(apiUrl).apiKey(apkKey).provider(provider).model(model).build();
    }
}
