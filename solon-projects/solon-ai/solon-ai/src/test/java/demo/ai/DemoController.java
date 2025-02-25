package demo.ai;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.web.sse.SseEvent;
import org.noear.solon.core.util.MimeType;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

/**
 * @author noear 2025/2/24 created
 */
@Controller
public class DemoController {
    @Inject
    ChatModel chatModel;

    @Inject
    EmbeddingModel embeddingModel;

    @Mapping("/chat")
    public ModelAndView index(Context ctx) throws IOException {
        return new ModelAndView("chat/index.ftl");
    }

    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("/chat/send1")
    public Flux<String> send1(String prompt) throws IOException {
        return Flux.interval(Duration.ofSeconds(1)) // 每秒生成一个数字
                .map(i -> {
                    String s = "{content:\""+"hello"+ i + "\", role:assistant}\n\n";
                    System.out.println(s);
                    return s;
                }) // 符合 SSE 协议的格式
                .take(6); // 只发送 6 条消息
    }

    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("/chat/send2")
    public Flux<SseEvent> send2(String prompt, Context ctx) throws IOException {
        return Flux.from(chatModel.prompt(prompt).stream())
                .filter(resp -> resp.hasChoices())
                .map(resp -> resp.getMessage())
                .map(msg -> {
                    System.out.println(msg.getContent());
                    return new SseEvent().data(msg.getContent());
                });
    }
}
