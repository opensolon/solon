package features.ai.chat;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/1/28 created
 */
public class OpenaiTest {
    //JQC6M0GTNPGSCEXZOBUGUX0HVHCOLDIMN6XOSSSA
    private static final Logger log = LoggerFactory.getLogger(OpenaiTest.class);
    private static final String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    private static final String apkKey = "sk-9f4415ddc570496581897c22e3d41a54";
    private static final String model = "deepseek-chat"; //deepseek-reasoner//deepseek-chat

    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl).apiKey(apkKey).model(model).build();

        //一次性返回
        ChatResponse resp = chatModel.prompt("hello").call();

        //打印消息
        log.info("{}", resp.getMessage());
    }

    @Test
    public void case2() throws Exception {
        ChatModel chatModel = ChatModel.of(apiUrl).apiKey(apkKey).model(model).build();

        //流返回
        Publisher<ChatResponse> publisher = chatModel.prompt("hello").stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
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
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey)
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
    public void case3_www() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey)
                .model(model)
                .globalFunctionAdd(new Tools())
                .build();

        ChatResponse resp = chatModel
                .prompt("solon 框架的作者是谁？")
                .call();

        //打印消息
        log.info("{}", resp.getMessage());
    }

    @Test
    public void case3_www_2() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey)
                .model(model)
                .build();

        ChatResponse resp = chatModel
                .prompt(ChatMessage.augment("solon 框架的作者是谁？", new Document()
                        .title("概述")
                        .url("https://solon.noear.org/article/about")))
                .call();

        //打印
        System.out.println(resp.getMessage());
    }

    @Test
    public void case4() throws Throwable {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey)
                .model(model)
                .build();

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel
                .prompt("今天杭州的天气情况？")
                .options(o -> o.functionAdd(new Tools()))
                .stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    log.info("{}", resp.getMessage());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                    doneLatch.countDown();
                }));

        doneLatch.await();
    }
}