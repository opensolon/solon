package features.ai;

import demo.ai.WeatherChatFunction;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.rx.SimpleSubscriber;
import org.noear.solon.test.SolonTest;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/1/28 created
 */
@SolonTest
public class OpenaiTest {
    private static final Logger log = LoggerFactory.getLogger(OpenaiTest.class);
    private static final String apiUrl = "https://api.deepseek.com/chat/completions";
    private static final String apkKey = "";
    private static final String model = "deepseek-chat"; //deepseek-reasoner//deepseek-chat

    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl).apiKey(apkKey).model(model).build();

        //一次性返回
        ChatResponse resp = chatModel.prompt("hello").call();

        //打印消息
        log.info("{}", resp.getChoices());
    }

    @Test
    public void case2() throws Exception {
        ChatModel chatModel = ChatModel.of(apiUrl).apiKey(apkKey).model(model).build();

        //流返回
        Publisher<ChatResponse> publisher = chatModel.prompt("hello").stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    log.info("{}", resp.getChoices());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

        doneLatch.await();
    }

    @Test
    public void case3() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .apiKey(apkKey)
                .model(model)
                .globalFunctionAdd(new WeatherChatFunction())
                .build();

        ChatResponse resp = chatModel
                .prompt("今天杭州的天气情况？")
                .call();

        //打印消息
        log.info("{}", resp.getChoices());
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
                .options(o -> o.functionAdd(new WeatherChatFunction()))
                .stream();

        CountDownLatch doneLatch = new CountDownLatch(1);
        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    log.info("{}", resp.getChoices());
                }).doOnComplete(() -> {
                    log.debug("::完成!");
                    doneLatch.countDown();
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

        doneLatch.await();
    }
}