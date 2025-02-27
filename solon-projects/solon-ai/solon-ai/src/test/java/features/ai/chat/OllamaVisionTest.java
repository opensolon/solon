package features.ai.chat;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.ChatSession;
import org.noear.solon.ai.chat.ChatSessionDefault;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.image.Image;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/1/28 created
 */
public class OllamaVisionTest {
    private static final Logger log = LoggerFactory.getLogger(OllamaVisionTest.class);
    private static final String apiUrl = "http://127.0.0.1:11434/api/chat";
    private static final String provider = "ollama";
    private static final String model = "llava:7b";//"llama3.2"; //deepseek-r1:1.5b;

    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .provider(provider) //需要指定供应商，用于识别接口风格（也称为方言）
                .model(model)
                .timeout(Duration.ofSeconds(300))
                .build();

        byte[] bytes = HttpUtils.http("https://solon.noear.org/img/solon/favicon256.png").exec("GET").bodyAsBytes();

        //一次性返回
        ChatResponse resp = chatModel.prompt(ChatMessage.ofUser("图里有人像吗？", Image.ofBase64(bytes)))
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
    }
}