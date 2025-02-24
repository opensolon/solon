package features.ai.chat;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/1/28 created
 */
public class DashscopeTest {
    private static final Logger log = LoggerFactory.getLogger(DashscopeTest.class);
    private static final String apiUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String apiKey = "sk-1ffe449611a74e61ad8e71e1b35a9858";
    private static final String provider = "dashscope";
    private static final String model = "qwen-turbo-latest";//"llama3.2"; //deepseek-r1:1.5b;

    ChatModel.Builder configChatModelBuilder(){
        return ChatModel.of(apiUrl)
                .apiKey(apiKey)
                .provider(provider)
                .model(model);
    }


    @Test
    public void case1() throws IOException {
        ChatModel chatModel = configChatModelBuilder().build();

        //一次性返回
        ChatResponse resp = chatModel.prompt("hello").call();

        //打印消息
        log.info("{}", resp.getMessage());
    }

    @Test
    public void case2() throws Exception {
        ChatModel chatModel = configChatModelBuilder().build();

        List<ChatMessage> messageList = new ArrayList<>();
        messageList.add(ChatMessage.ofUser("hello"));

        //流返回
        Publisher<ChatResponse> publisher = chatModel.prompt(messageList).stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    messageList.add(resp.getMessage());
                    log.info("{}", resp.getMessage());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

        doneLatch.await();
    }

    @Test
    public void case3_wather() throws IOException {
        ChatModel chatModel = configChatModelBuilder()
                .globalFunctionAdd(new Tools())
                .build();

        ChatResponse resp = chatModel
                .prompt("今天杭州的天气情况？现在几点了?")
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
        log.info("\n-----------答案-----------\n{}", resp.getMessage().getContent());
    }

    @Test
    public void case3_www() throws IOException {
        ChatModel chatModel = configChatModelBuilder()
                .globalFunctionAdd(new Tools())
                .build();

        ChatResponse resp = chatModel
                .prompt("solon 框架的作者是谁？")
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
        log.info("\n-----------答案-----------\n{}", resp.getMessage().getContent());
    }

    @Test
    public void case4() throws Throwable {
        ChatModel chatModel = configChatModelBuilder().build();

        List<ChatMessage> messageList = new ArrayList<>();
        messageList.add(ChatMessage.ofUser("今天杭州的天气情况？"));

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel
                .prompt(messageList)
                .options(o -> o.functionAdd(new Tools()))
                .stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    messageList.add(resp.getMessage());
                    log.info("{}", resp.getMessage());
                    log.info("\n-----------答案-----------\n{}", resp.getMessage().getContent());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

        doneLatch.await();
    }
}