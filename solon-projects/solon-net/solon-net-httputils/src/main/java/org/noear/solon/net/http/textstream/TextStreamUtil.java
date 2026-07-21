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
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

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
     * <p>流结束/取消/出错时关闭 response，以归还连接</p>
     */
    public static Flux<String> parseLineStream(HttpResponse response) {
        return parseLineStream(response.body(), response.contentCharset())
                .doFinally(signal -> RunUtil.runAndTry(response::close));
    }

    private static final int DEFAULT_BUFFER_SIZE = 256;

    private static Flux<String> parseLineStream(InputStream inputStream, Charset charset) {
        Charset finalCharset = (charset == null) ? Charset.forName(Solon.encoding()) : charset;

        return Flux.<String>create(sink -> {
            CloseTrackableBufferedReader reader = new CloseTrackableBufferedReader(new InputStreamReader(inputStream, finalCharset), 1024);

            // 取消时立即关闭 reader，使阻塞中的 readLine() 抛出 IOException 退出
            sink.onDispose(() -> RunUtil.runAndTry(reader::close));

            try {
                String line;
                while (!sink.isCancelled() && !reader.isClosed() && (line = reader.readLine()) != null) {
                    sink.next(line);
                }
                sink.complete();
            } catch (Exception e) {
                // 如果已取消（超时等），不向下游传播错误
                if (!sink.isCancelled()) {
                    sink.error(e);
                }
            }
        }, FluxSink.OverflowStrategy.BUFFER)
                .subscribeOn(Schedulers.boundedElastic());
    }


    /**
     * 将输入流解析为 SSE 事件流 Flux
     */
    public static Flux<ServerSentEvent> parseSseStream(InputStream inputStream) {
        return parseSseStream(inputStream, null);
    }

    /**
     * 将 HTTP 响应解析为 SSE 事件流 Flux
     * <p>流结束/取消/出错时关闭 response，以归还连接</p>
     */
    public static Flux<ServerSentEvent> parseSseStream(HttpResponse response) {
        return parseSseStream(response.body(), response.contentCharset())
                .doFinally(signal -> RunUtil.runAndTry(response::close));
    }

    private static Flux<ServerSentEvent> parseSseStream(InputStream inputStream, Charset charset) {
        Charset finalCharset = (charset == null) ? Charset.forName(Solon.encoding()) : charset;

        return Flux.<ServerSentEvent>create(sink -> {
            CloseTrackableBufferedReader reader = new CloseTrackableBufferedReader(new InputStreamReader(inputStream, finalCharset), 1024);

            // 取消时立即关闭 reader，使阻塞中的 readLine() 抛出 IOException 退出
            sink.onDispose(() -> RunUtil.runAndTry(reader::close));

            // SSE 上下文
            Map<String, String> meta = new HashMap<>();
            StringBuilder data = new StringBuilder();

            try {
                String line;
                while (!sink.isCancelled() && !reader.isClosed() && (line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        // 1. 遇到空行：只要 meta 或 data 有内容，就分派事件
                        if (data.length() > 0 || !meta.isEmpty()) {
                            String dataStr = data.toString();
                            ServerSentEvent event = new ServerSentEvent(new HashMap<>(meta), dataStr);
                            sink.next(event);

                            meta.clear();
                            data.setLength(0);

                            // 使用 trim 后的 event.getData() 进行 [DONE] 检测，
                            // 与 ServerSentEvent 构造器内 data.trim() 保持一致
                            if ("[DONE]".equals(event.getData())) {
                                sink.complete();
                                return;
                            }
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
                            if (data.length() > 0) data.append("\n");
                            data.append(value);
                        } else {
                            // event, id, retry 等都在这里
                            meta.put(name, value);
                        }
                    }
                }
                sink.complete();
            } catch (Exception e) {
                // 如果已取消（超时等），不向下游传播错误
                if (!sink.isCancelled()) {
                    sink.error(e);
                }
            }
        }, FluxSink.OverflowStrategy.BUFFER)
                .subscribeOn(Schedulers.boundedElastic());
    }
}