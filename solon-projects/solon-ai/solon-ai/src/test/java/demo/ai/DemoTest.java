package demo.ai;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.*;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;

import java.io.IOException;

/**
 * @author noear 2025/1/28 created
 */
public class DemoTest {
    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .apiKey("sm-0aLe9LC3Yk6FYuKD")
                .build();

        //一次性返回
        ChatResponse resp = chatModel
                .prompt(ChatMessage.ofUser("介绍下 solon 框架的插件扩展体系"))
                .options(o -> o.temperature(0.8F))
                .call();

        //打印消息
        for (ChatMessage msg : resp.getMessages()) {
            System.out.println(msg.getContent());
        }
    }

    @Test
    public void case2() {
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .provider("ollama")
                .model("deepseek-r1")
                .build();

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel
                .prompt(ChatMessage.ofUser("介绍下 solon 框架的插件扩展体系"))
                .stream();

        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    for (ChatMessage msg : resp.getMessages()) {
                        System.out.println(msg.getContent());
                    }
                }));
    }
}