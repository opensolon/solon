package demo.ai;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.*;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear 2025/1/28 created
 */
public class DemoTest {
    private static final Logger log = LoggerFactory.getLogger(DemoTest.class);

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
        log.info("{}", resp.getChoices());
    }

    @Test
    public void case2() {
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .provider("ollama")
                .model("deepseek-r1")
                .headerSet("X-Demo", "test")
                .build();

        //流返回(sse)
        Publisher<ChatResponse> publisher = chatModel
                .prompt("介绍下 solon 框架的插件扩展体系")
                .stream();

        publisher.subscribe(new SimpleSubscriber<ChatResponse>()
                .doOnNext(resp -> {
                    log.info("{}", resp.getChoices());
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
        log.info("{}", resp.getChoices());
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