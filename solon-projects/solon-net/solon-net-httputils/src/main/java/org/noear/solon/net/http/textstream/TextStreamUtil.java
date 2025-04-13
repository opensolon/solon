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

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.SimpleSubscription;
import org.reactivestreams.Subscriber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 文本流解析工具
 *
 * @author noear
 * @since 3.1
 */
public class TextStreamUtil {
    /**
     * 解析文件行流
     *
     * @param inputStream 输入流
     * @param subscriber  订阅者
     * @deprecated 3.1 {@link #parseLineStream(InputStream, Subscriber)}
     */
    @Deprecated
    public static void parseTextStream(InputStream inputStream, Subscriber<? super String> subscriber) throws IOException {
        parseLineStream(inputStream, subscriber);
    }

    /**
     * 解析文件行流
     *
     * @param inputStream 输入流
     * @param subscriber  订阅者
     */
    public static void parseLineStream(InputStream inputStream, Subscriber<? super String> subscriber) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1024);
        subscriber.onSubscribe(new SimpleSubscription().onRequest((subscription, l) -> {
            onLineStreamRequestDo(reader, subscriber, subscription, l);
        }));
    }

    private static void onLineStreamRequestDo(BufferedReader reader, Subscriber<? super String> subscriber, SimpleSubscription subscription, long l) {
        try {
            while (l > 0) {
                if (subscription.isCancelled()) {
                    return;
                }

                String textLine = reader.readLine();

                if (textLine == null) {
                    break;
                } else {
                    subscriber.onNext(textLine);
                    l--; //提交后再减
                }
            }
            //完成需求
            subscriber.onComplete();
            reader.close();
        } catch (Throwable err) {
            RunUtil.runAndTry(reader::close);
            subscriber.onError(err);
        }
    }

    /**
     * 解析服务推送事件流
     *
     * @param inputStream 输入流
     * @param subscriber  订阅者
     * @deprecated 3.1 {@link #parseSseStream(InputStream, Subscriber)}
     */
    @Deprecated
    public static void parseEventStream(InputStream inputStream, Subscriber<? super ServerSentEvent> subscriber) throws IOException {
        parseSseStream(inputStream, subscriber);
    }

    /**
     * 解析服务推送事件流
     *
     * @param inputStream 输入流
     * @param subscriber  订阅者
     */
    public static void parseSseStream(InputStream inputStream, Subscriber<? super ServerSentEvent> subscriber) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1024);
        subscriber.onSubscribe(new SimpleSubscription().onRequest((subscription, l) -> {
            onSseStreamRequestDo(reader, subscriber, subscription, l);
        }));
    }

    private static void onSseStreamRequestDo(BufferedReader reader, Subscriber<? super ServerSentEvent> subscriber, SimpleSubscription subscription, long l) {
        try {
            Map<String, String> meta = new HashMap<>();
            StringBuilder data = new StringBuilder();

            while (l > 0) {
                if (subscription.isCancelled()) {
                    return;
                }

                String textLine = reader.readLine();

                if (textLine == null) {
                    break;
                } else {
                    if (textLine.isEmpty()) {
                        if (data.length() > 0) {
                            subscriber.onNext(new ServerSentEvent(meta, data.toString()));
                            l--; //提交后再减
                            meta = new HashMap<>();
                            data.setLength(0);
                        }
                    } else if (textLine.startsWith("data:")) {
                        String content = textLine.substring("data:".length());
                        if (data.length() > 0) {
                            data.append("\n");
                        }

                        data.append(content.trim());
                    } else {
                        int flagIdx = textLine.indexOf(':');
                        if (flagIdx > 0) {
                            meta.put(textLine.substring(0, flagIdx).trim(),
                                    textLine.substring(flagIdx + 1).trim());
                        }
                    }
                }
            }

            //完成需求
            subscriber.onComplete();
            reader.close();
        } catch (Throwable err) {
            RunUtil.runAndTry(reader::close);
            subscriber.onError(err);
        }
    }
}