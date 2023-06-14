package org.noear.solon.web.sse.demo.controller;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;
import org.noear.solon.web.sse.SseEmitter;
import org.noear.solon.web.sse.SseEvent;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SseController {
    static Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    @Get
    @Mapping("/sse/{id}")
    public void sse(String id) throws Throwable {
        SseEmitter emitter = new SseEmitter(1L);

        sseEmitterMap.put(id, emitter);
        emitter.start();
        System.out.println("Started");
    }

    @Get
    @Mapping("/sse/put/{id}")
    public String ssePut(String id) {
        SseEmitter emitter = sseEmitterMap.get(id);
        if (emitter == null) {
            return "No user: " + id;
        }

        String msg = "this is msg -> " + System.currentTimeMillis();
        System.out.println(msg);
        emitter.send(msg);
        emitter.send(new SseEvent().id(Utils.guid()).name("update").data(msg));
        emitter.send(ONode.stringify(Result.succeed(msg)));

        return "Ok";
    }

    @Get
    @Mapping("/sse/del/{id}")
    public String sseDel(String id) throws Throwable {
        SseEmitter emitter = sseEmitterMap.get(id);
        if (emitter != null) {
            sseEmitterMap.remove(id);
            emitter.stop();
        }

        return "Ok";
    }
}
