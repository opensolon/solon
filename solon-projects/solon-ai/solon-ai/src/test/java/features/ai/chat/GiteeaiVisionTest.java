package features.ai.chat;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.image.Image;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

/**
 * @author noear 2025/1/28 created
 */
public class GiteeaiVisionTest {
    private static final Logger log = LoggerFactory.getLogger(GiteeaiVisionTest.class);
    private static final String apiUrl = "https://ai.gitee.com/v1/chat/completions";
    private static final String apkKey = "PE6JVMP7UQI81GY6AZ0J8WEWWLFHWHROG15XUP18";
    private static final String model = "InternVL2-8B";//"DeepSeek-V3"; //deepseek-reasoner//deepseek-chat

    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey) //需要指定供应商，用于识别接口风格（也称为方言）
                .model(model)
                .timeout(Duration.ofSeconds(300))
                .build();

        String imageUrl = "https://solon.noear.org/img/369a9093918747df8ab0a5ccc314306a.png";

        byte[] bytes = HttpUtils.http(imageUrl).exec("GET").bodyAsBytes();

        //一次性返回
        ChatResponse resp = chatModel.prompt(ChatMessage.ofUser("这图里有方块吗？", Image.ofBase64(bytes)))
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
    }

    @Test
    public void case2() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey) //需要指定供应商，用于识别接口风格（也称为方言）
                .model(model)
                .timeout(Duration.ofSeconds(300))
                .build();

        String imageUrl = "https://solon.noear.org/img/369a9093918747df8ab0a5ccc314306a.png";

        //一次性返回
        ChatResponse resp = chatModel.prompt(ChatMessage.ofUser("这图里有方块吗？", Image.ofUrl(imageUrl)))
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
    }
}