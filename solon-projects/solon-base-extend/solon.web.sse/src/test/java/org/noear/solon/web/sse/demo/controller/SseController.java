package org.noear.solon.web.sse.demo.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.web.sse.SseEmitter;

@Controller
public class SseController {

    @Mapping("/sse")
    public void s() throws Exception {
        SseEmitter emitter = SseEmitter.builder()
                .onCompletion(() -> {
                    System.out.println("completion");
                })
                .onError((e) -> {
                    System.out.println("error ");
                })
                .build();

        for (int i = 0; i < 10; i++) {
            String msg = "this is msg ->" + i;
            System.out.println(msg);
            emitter.send(msg);
            Thread.sleep(100);
        }
        emitter.complete();
        System.out.println("0k");
    }
}
