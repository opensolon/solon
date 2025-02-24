package features.ai.chat;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.ChatSession;
import org.noear.solon.ai.chat.ChatSessionDefault;
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
public class OllamaTest {
    private static final Logger log = LoggerFactory.getLogger(OllamaTest.class);
    private static final String apiUrl = "http://127.0.0.1:11434/api/chat";
    private static final String provider = "ollama";
    private static final String model = "llama3.2";//"llama3.2"; //deepseek-r1:1.5b;

    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .provider(provider) //需要指定供应商，用于识别接口风格（也称为方言）
                .model(model)
                .build();

        //一次性返回
        ChatResponse resp = chatModel.prompt("hello").call();

        //打印消息
        log.info("{}", resp.getMessage());
    }

    @Test
    public void case2() throws Exception {
        ChatModel chatModel = ChatModel.of(apiUrl).provider(provider).model(model).build();

        ChatSession chatSession = new ChatSessionDefault();
        chatSession.addMessage(ChatMessage.ofUser("hello"));

        //流返回
        Publisher<ChatResponse> publisher = chatModel.prompt(chatSession).stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    chatSession.addMessage(resp.getMessage());
                    log.info("{}", resp.getMessage());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

        doneLatch.await();

        //序列化测试
        String ndjson1 = chatSession.toNdjson();
        System.out.println(ndjson1);

        chatSession.clear();
        chatSession.loadNdjson(ndjson1);
        String ndjson2 = chatSession.toNdjson();
        System.out.println(ndjson2);
        assert ndjson1.equals(ndjson2);
    }

    @Test
    public void case3_wather() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .provider(provider)
                .model(model)
                .globalFunctionAdd(new Tools())
                .build();

        ChatResponse resp = chatModel
                .prompt("今天杭州的天气情况？")
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
    }

    @Test
    public void case4() throws Throwable {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .provider(provider)
                .model(model)
                .build();

        ChatSession chatSession = new ChatSessionDefault();
        chatSession.addMessage(ChatMessage.ofUser("今天杭州的天气情况？"));

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel
                .prompt(chatSession)
                .options(o -> o.functionAdd(new Tools()))
                .stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    chatSession.addMessage(resp.getMessage());
                    log.info("{}", resp.getMessage());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

        doneLatch.await();


        //序列化测试
        String ndjson1 = chatSession.toNdjson();
        System.out.println(ndjson1);

        chatSession.clear();
        chatSession.loadNdjson(ndjson1);
        String ndjson2 = chatSession.toNdjson();
        System.out.println(ndjson2);
        assert ndjson1.equals(ndjson2);
    }
}