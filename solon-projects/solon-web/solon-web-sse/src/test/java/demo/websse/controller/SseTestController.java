package demo.websse.controller;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.web.sse.SseEvent;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author noear 2025/4/14 created
 */
@Controller
public class SseTestController {
    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("test/sse1")
    public Flux<SseEvent> sse1() throws IOException {
        return Flux.just(
                new SseEvent().data("hello"),
                new SseEvent().id(Utils.guid()).name("update").data("test"));
    }

    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("test/sse2")
    public Flux<SseEvent> sse2() throws IOException {
        return Flux.create((sink) -> {
            sink.next(new SseEvent().data("hello"));
            sink.next(new SseEvent().id(Utils.guid()).name("update").data("test"));
            sink.complete();
        });
    }

    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("test/sse3")
    public Flux<SseEvent> sse3() throws IOException {
        return Flux.create((sink) -> {
            sink.next(new SseEvent().data("hello"));
            sink.next(new SseEvent().id(Utils.guid()).name("update").data("test"));
        });
    }
}
