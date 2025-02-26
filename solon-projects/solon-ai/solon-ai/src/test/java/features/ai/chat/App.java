package features.ai.chat;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2025/2/10 created
 */
@Configuration
@SolonTest
public class App {
    @Bean
    public ChatModel build(@Inject("${solon.ai.chat.llama3}") ChatConfig config) {
        return ChatModel.of(config)
                .build();
    }

    @Inject
    ChatModel chatModel;

    @Test
    public void case1() throws IOException {
        //一次性返回
        ChatResponse resp = chatModel.prompt("hello").call();

        //打印消息
        System.out.println(resp.getChoices());
    }
}
