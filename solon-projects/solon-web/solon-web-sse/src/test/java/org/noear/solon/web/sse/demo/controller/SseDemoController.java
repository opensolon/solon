/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.sse.demo.controller;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.web.sse.SseEmitter;
import org.noear.solon.web.sse.SseEvent;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SseDemoController {
    static Map<String, SseEmitter> emitterMap = new HashMap<>();


    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("/sse/rx")
    public Flux<SseEvent> sse_rx(String id) throws IOException {
        return Flux.just(
                new SseEvent().data("hello"),
                new SseEvent().id(Utils.guid()).name("update").data("test"));
    }

    @Produces(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE)
    @Mapping("/sse/rx2")
    public Flux<User> sse_rx2() throws IOException {
        return Flux.just(
                new User("d1"),
                new User("d2"));
    }

    @Mapping("/sse/{id}")
    public SseEmitter sse(String id) throws IOException {
        SseEmitter emitter = new SseEmitter(-1L)
                .onCompletion(() -> {
                    emitterMap.remove(id);
                    System.out.println("::onCompletion");

                })
                .onError(e -> {
                    e.printStackTrace();
                });

        emitterMap.put(id, emitter);
        emitter.send("你好！");

        return emitter;
    }

    @Mapping("/sse2/{id}")
    public SseEmitter sse2(String id) {
        //// 超时时间设置为3s，用于演示客户端自动重连
        return new SseEmitter(3000L)
                .onCompletion(() -> {
                    emitterMap.remove(id);
                    System.out.println("::onCompletion");

                })
                .onError(e -> {
                    e.printStackTrace();
                })
                .onInited(s -> {
                    emitterMap.put(id, s);
                    // 设置前端的重试时间为1s
                    s.send(new SseEvent().data("连接成功").reconnectTime(1000L));
                });
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

        return "Ok";
    }

    @Get
    @Mapping("/sse/del/{id}")
    public String sseDel(String id) throws IOException{
        SseEmitter emitter = emitterMap.get(id);
        if (emitter != null) {
            emitter.complete();
        }

        return "Ok";
    }
}
