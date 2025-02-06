package demo.ai;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.*;
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
    @Test
    public void case1() throws IOException {
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .apiKey("sm-0aLe9LC3Yk6FYuKD")
                .build();

        //一次性返回
        ChatResponse resp = chatModel
                .prompt("介绍下 solon 框架的插件扩展体系")
                .options(o -> o.temperature(0.8F))
                .call();

        //打印消息
        for (ChatMessage msg : resp.getMessages()) {
            System.out.println(msg.getContent());
        }
    }

    @Test
    public void case1_2() throws IOException {
        //过程参考：https://blog.csdn.net/owenc1/article/details/142812656
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .apiKey("sm-0aLe9LC3Yk6FYuKD")
                .headerAdd("X-Demo","test")
                .functionAdd("get_weather", decl -> decl
                        .description("获取指定城市的天气情况")
                        .paramAdd("location", "string", "根据用户提到的地点推测城市")
                )
                .build();

        //一次性返回
        ChatResponse resp = chatModel
                .prompt(ChatMessage.ofUser("今天的杭州天气怎么样？", "get_weather"))
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
                .prompt("介绍下 solon 框架的插件扩展体系")
                .stream();

        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    for (ChatMessage msg : resp.getMessages()) {
                        System.out.println(msg.getContent());
                    }
                }));
    }

    @Test
    public void case3() throws IOException {
        ChatModel chatModel = ChatModel.of("http://localhost:8080").build();

        //历史提示语（记忆）
        ChatResponse resp = chatModel
                .prompt(ChatMessage.ofSystem("欢迎使用 ..."),
                        ChatMessage.ofUser("介绍下 solon 框架的插件扩展体系"),
                        ChatMessage.ofAssistant("..."),
                        ChatMessage.ofUser("..."))
                .options(o -> o.temperature(0.8F))
                .call();

        //打印消息
        for (ChatMessage msg : resp.getMessages()) {
            System.out.println(msg.getContent());
        }
    }

    @Test
    public void case4() throws Throwable {
        ChatModel chatModel = ChatModel.of("http://localhost:8080").build();

        FlowEngine flowEngine = FlowEngine.newInstance();
        flowEngine.load(Chain.parseByUri("classpath:flow/case4.yml"));

        ChainContext ctx = new ChainContext();
        ctx.put("chatModel", chatModel);

        flowEngine.eval("ai-1");
    }
}