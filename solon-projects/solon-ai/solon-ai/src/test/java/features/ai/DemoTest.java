package features.ai;

import demo.ai.WeatherChatFunction;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;

import java.io.IOException;

/**
 * @author noear 2025/1/28 created
 */
public class DemoTest {
    private static final String apiUrl = "http://127.0.0.1:11434/api/chat";
    private static final String model = "deepseek-r1:1.5b";//"llama3.2";

    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl).model(model).build();

        //一次性返回
        ChatResponse resp = chatModel.prompt("hello").call();

        //打印消息
        System.out.println(resp.getMessage().getContent());
        assert resp.getMessage().getContent().contains("Hello");
    }

    @Test
    public void case2() {
        ChatModel chatModel = ChatModel.of(apiUrl).model(model).build();

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel.prompt("hello").stream();

        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    System.out.println(resp.getMessage().getContent());
                }).doOnComplete(() -> {
                    System.out.println("::完成!");
                }).doOnError(err -> {
                    err.printStackTrace();
                }));
    }

    @Test
    public void case3() throws IOException {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .model(model)
                .globalFunctionAdd(new WeatherChatFunction())
                .build();

        //历史提示语（记忆）
        ChatResponse resp = chatModel
                .prompt("今天杭州的天气情况？")
                .call();

        //打印消息
        System.out.println(resp.getMessage().getContent());
    }

    @Test
    public void case4() throws Throwable {
        ChatModel chatModel = ChatModel.of(apiUrl)
                .model(model)
                .build();

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel
                .prompt("今天杭州的天气情况？")
                .options(o -> o.functionAdd(new WeatherChatFunction()))
                .stream();

        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    System.out.println(resp.getMessage().getContent());
                }).doOnComplete(() -> {
                    System.out.println("::完成!");
                }).doOnError(err -> {
                    err.printStackTrace();
                }));

    }
}