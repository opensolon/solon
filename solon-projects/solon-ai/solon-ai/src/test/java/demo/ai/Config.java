package demo.ai;

import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2025/2/24 created
 */
@Configuration
public class Config {

    @Bean
    public ChatModel chatModel(@Inject("${solon.ai.chat.qwen}") ChatConfig config) {
        return ChatModel.of(config).build();
    }

    @Bean
    public EmbeddingModel embeddingModel(@Inject("${solon.ai.embed.bge-m3}") EmbeddingConfig config) {
        return EmbeddingModel.of(config).build();
    }
}
