package org.noear.solon.web.sse.demo.controller;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;
import org.noear.solon.web.sse.SseEmitter;
import org.noear.solon.web.sse.SseEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SseDemoController {
    static Map<String, SseEmitter> emitterMap = new HashMap<>();

    @Mapping("/sse/{id}")
    public SseEmitter sse(String id) throws Throwable {
        return new SseEmitter(0L)
                .onCompletion(() -> {
                    emitterMap.remove(id);
                    System.out.println("::onCompletion");

                })
                .onError(e -> {
                    emitterMap.remove(id);
                    System.out.println("::onError");
                })
                .release(e -> emitterMap.put(id, e));
    }

    @Get
    @Mapping("/sse/put/{id}")
    public String ssePut(String id) throws IOException {
        SseEmitter emitter = emitterMap.get(id);
        if (emitter == null) {
            return "No user: " + id;
        }

        String msg = "test msg -> " + System.currentTimeMillis();
        System.out.println(msg);
        emitter.send(msg);
        emitter.send(new SseEvent().id(Utils.guid()).name("update").data(msg));
        emitter.send(ONode.stringify(Result.succeed(msg)));

        return "Ok";
    }

    @Get
    @Mapping("/sse/del/{id}")
    public String sseDel(String id) throws Throwable {
        SseEmitter emitter = emitterMap.get(id);
        if (emitter != null) {
            emitterMap.remove(id);
            emitter.complete();
        }

        return "Ok";
    }
}
