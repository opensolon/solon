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
package org.noear.solon.net.http.textstream;

import org.noear.solon.Solon;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.http.HttpResponse;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 文本流解析工具 (Reactor 驱动版)
 *
 * @author noear
 * @since 3.1
 * @since 3.9.1
 */
public class TextStreamUtil {
    /**
     * 将输入流解析为逐行文本流 Flux
     */
    public static Flux<String> parseLineStream(InputStream inputStream) {
        return parseLineStream(inputStream, null);
    }

    /**
     * 将 HTTP 响应解析为逐行文本流 Flux
     */
    public static Flux<String> parseLineStream(HttpResponse response) {
        return parseLineStream(response.body(), response.contentCharset());
    }

    private static Flux<String> parseLineStream(InputStream inputStream, Charset charset) {
        Charset finalCharset = (charset == null) ? Charset.forName(Solon.encoding()) : charset;

        return Flux.generate(
                () -> new CloseTrackableBufferedReader(new InputStreamReader(inputStream, finalCharset), 1024),
                (reader, sink) -> {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            sink.complete();
                        } else {
                            sink.next(line);
                        }
                    } catch (Exception e) {
                        sink.error(e);
                    }
                    return reader;
                },
                reader -> RunUtil.runAndTry(reader::close)
        );
    }



    // --- SSE 相关 ---


    /**
     * 将输入流解析为 SSE 事件流 Flux
     */
    public static Flux<ServerSentEvent> parseSseStream(InputStream inputStream) {
        return parseSseStream(inputStream, null);
    }

    /**
     * 将 HTTP 响应解析为 SSE 事件流 Flux
     */
    public static Flux<ServerSentEvent> parseSseStream(HttpResponse response) {
        return parseSseStream(response.body(), response.contentCharset());
    }

    private static Flux<ServerSentEvent> parseSseStream(InputStream inputStream, Charset charset) {
        Charset finalCharset = (charset == null) ? Charset.forName(Solon.encoding()) : charset;

        return Flux.generate(
                () -> new SseContext(new CloseTrackableBufferedReader(new InputStreamReader(inputStream, finalCharset), 1024)),
                (ctx, sink) -> {
                    try {
                        String line;
                        while ((line = ctx.reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                // 1. 遇到空行：只要 meta 或 data 有内容，就分派事件
                                if (ctx.data.length() > 0 || !ctx.meta.isEmpty()) {
                                    String dataStr = ctx.data.toString();
                                    sink.next(new ServerSentEvent(new HashMap<>(ctx.meta), dataStr));

                                    ctx.meta.clear();
                                    ctx.data.setLength(0);

                                    if ("[DONE]".equals(dataStr)) {
                                        sink.complete();
                                    }
                                    return ctx;
                                }
                            } else if (line.startsWith(":")) {
                                // 2. 忽略注释行
                                continue;
                            } else {
                                // 3. 通用解析逻辑：拆分 name 和 value
                                int flagIdx = line.indexOf(':');
                                String name, value;
                                if (flagIdx > -1) {
                                    name = line.substring(0, flagIdx);
                                    int valIdx = flagIdx + 1;
                                    // 规范：如果冒号后紧跟一个空格，则去掉该空格
                                    if (line.length() > valIdx && line.charAt(valIdx) == ' ') {
                                        valIdx++;
                                    }
                                    value = line.substring(valIdx);
                                } else {
                                    name = line;
                                    value = "";
                                }

                                // 4. 根据字段名分发
                                if ("data".equals(name)) {
                                    if (ctx.data.length() > 0) ctx.data.append("\n");
                                    ctx.data.append(value);
                                } else {
                                    // event, id, retry 等都在这里
                                    ctx.meta.put(name, value);
                                }
                            }
                        }
                        sink.complete();
                    } catch (Exception e) {
                        sink.error(e);
                    }
                    return ctx;
                },
                ctx -> RunUtil.runAndTry(ctx.reader::close)
        );
    }

    /**
     * 内部状态类，用于在 generate 回调间保持上下文
     */
    private static class SseContext {
        final CloseTrackableBufferedReader reader;
        final Map<String, String> meta = new HashMap<>();
        final StringBuilder data = new StringBuilder();

        SseContext(CloseTrackableBufferedReader reader) {
            this.reader = reader;
        }
    }

    //---------

    /**
     * @deprecated 3.9.1
     */
    @Deprecated
    public static void parseLineStream(HttpResponse response, Subscriber<? super String> subscriber) {
        parseLineStream(response.body(), response.contentCharset(), subscriber);
    }

    /**
     * @deprecated 3.9.1
     */
    @Deprecated
    public static void parseLineStream(InputStream inputStream, Charset charset, Subscriber<? super String> subscriber) {
        parseLineStream(inputStream, charset).subscribe(subscriber);
    }

    /**
     * @deprecated 3.1 {@link #parseLineStream(InputStream, Charset, Subscriber)}
     */
    @Deprecated
    public static void parseTextStream(InputStream inputStream, Subscriber<? super String> subscriber) throws IOException {
        parseLineStream(inputStream, null, subscriber);
    }


    /**
     * @deprecated 3.1 {@link #parseSseStream(InputStream, Charset, Subscriber)}
     */
    @Deprecated
    public static void parseEventStream(InputStream inputStream, Subscriber<? super ServerSentEvent> subscriber) throws IOException {
        parseSseStream(inputStream, null, subscriber);
    }

    /**
     * @deprecated 3.9.1
     */
    @Deprecated
    public static void parseSseStream(HttpResponse response, Subscriber<? super ServerSentEvent> subscriber) {
        parseSseStream(response.body(), response.contentCharset(), subscriber);
    }

    /**
     * @deprecated 3.9.1
     */
    @Deprecated
    public static void parseSseStream(InputStream inputStream, Charset charset, Subscriber<? super ServerSentEvent> subscriber) {
        parseSseStream(inputStream, charset).subscribe(subscriber);
    }
}