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
import org.noear.solon.rx.SimpleSubscription;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
     * @deprecated 3.1 {@link #parseLineStream(InputStream, Charset, Subscriber)}
     */
    @Deprecated
    public static void parseTextStream(InputStream inputStream, Subscriber<? super String> subscriber) throws IOException {
        parseLineStream(inputStream, null, subscriber);
    }

    /**
     * 解析文件行流
     *
     * @param inputStream 输入流
     * @return 发布者
     */
    public static Publisher<String> parseLineStream(InputStream inputStream) {
        return subscriber -> {
            try {
                TextStreamUtil.parseLineStream(inputStream, null, subscriber);
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }

    /**
     * 解析文件行流
     *
     * @param response 响应
     * @return 发布者
     */
    public static Publisher<String> parseLineStream(HttpResponse response) {
        return subscriber -> {
            try {
                TextStreamUtil.parseLineStream(response.body(), response.contentEncoding(), subscriber);
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }

    /**
     * 解析文件行流
     *
     * @param response 响应
     * @return 发布者
     */
    public static void parseLineStream(HttpResponse response, Subscriber<? super String> subscriber) {
        TextStreamUtil.parseLineStream(response.body(), response.contentEncoding(), subscriber);
    }

    /**
     * 解析文件行流
     *
     * @param inputStream 输入流
     * @param subscriber  订阅者
     */
    public static void parseLineStream(InputStream inputStream, Charset charset, Subscriber<? super String> subscriber) {
        if (charset == null) {
            charset = Charset.forName(Solon.encoding());
        }

        CloseTrackableBufferedReader reader = new CloseTrackableBufferedReader(new InputStreamReader(inputStream, charset), 1024);
        subscriber.onSubscribe(new SimpleSubscription().onRequest((subscription, l) -> {
            onLineStreamRequestDo(reader, subscriber, subscription, l);
        }));
    }

    private static void onLineStreamRequestDo(CloseTrackableBufferedReader reader, Subscriber<? super String> subscriber, SimpleSubscription subscription, long l) {
        if (reader.isClosed()) {
            //并发信号时；有些线程关闭了，仍可能会探测进来
            return;
        }

        try {
            while (l > 0) {
                if (subscription.isCancelled()) {
                    RunUtil.runAndTry(reader::close);
                    return;
                }

                if (reader.isClosed()) {
                    //并发信号时；有些线程关闭了，但有些线程还在读（这里要挡一下）
                    subscriber.onComplete();
                    return;
                }

                String textLine = reader.readLine();

                if (textLine == null) {
                    //完成需求
                    RunUtil.runAndTry(reader::close);
                    subscriber.onComplete(); //可能会再次触发信号（所以，要先关）
                    return;
                } else {
                    subscriber.onNext(textLine);
                    l--; //提交后再减
                }
            }
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
     * @deprecated 3.1 {@link #parseSseStream(InputStream, Charset, Subscriber)}
     */
    @Deprecated
    public static void parseEventStream(InputStream inputStream, Subscriber<? super ServerSentEvent> subscriber) throws IOException {
        parseSseStream(inputStream, null, subscriber);
    }

    /**
     * 解析服务推送事件流
     *
     * @param inputStream 输入流
     * @return 发布者
     */
    public static Publisher<ServerSentEvent> parseSseStream(InputStream inputStream) {
        return subscriber -> {
            try {
                TextStreamUtil.parseSseStream(inputStream, null, subscriber);
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }

    /**
     * 解析服务推送事件流
     *
     * @param response 响应
     * @return 发布者
     */
    public static Publisher<ServerSentEvent> parseSseStream(HttpResponse response) {
        return subscriber -> {
            try {
                TextStreamUtil.parseSseStream(response.body(), response.contentEncoding(), subscriber);
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };
    }

    /**
     * 解析服务推送事件流
     *
     * @param response 响应
     * @return 发布者
     */
    public static void parseSseStream(HttpResponse response, Subscriber<? super ServerSentEvent> subscriber) {
        TextStreamUtil.parseSseStream(response.body(), response.contentEncoding(), subscriber);
    }

    /**
     * 解析服务推送事件流
     *
     * @param inputStream 输入流
     * @param subscriber  订阅者
     */
    public static void parseSseStream(InputStream inputStream, Charset charset, Subscriber<? super ServerSentEvent> subscriber) {
        if (charset == null) {
            charset = Charset.forName(Solon.encoding());
        }

        CloseTrackableBufferedReader reader = new CloseTrackableBufferedReader(new InputStreamReader(inputStream, charset), 1024);
        subscriber.onSubscribe(new SimpleSubscription().onRequest((subscription, l) -> {
            onSseStreamRequestDo(reader, subscriber, subscription, l);
        }));
    }

    private static void onSseStreamRequestDo(CloseTrackableBufferedReader reader, Subscriber<? super ServerSentEvent> subscriber, SimpleSubscription subscription, long l) {
        if (reader.isClosed()) {
            //并发信号时；有些线程关闭了，仍可能会探测进来
            return;
        }

        try {
            Map<String, String> meta = new HashMap<>();
            StringBuilder data = new StringBuilder();

            while (l > 0) {
                if (subscription.isCancelled()) {
                    RunUtil.runAndTry(reader::close);
                    return;
                }

                if (reader.isClosed()) {
                    //并发信号时；有些线程关闭了，但有些线程还在读（这里要挡一下）
                    subscriber.onComplete();
                    return;
                }

                String textLine = reader.readLine();

                if (textLine == null) {
                    //完成需求
                    RunUtil.runAndTry(reader::close);
                    subscriber.onComplete(); //可能会再次触发信号（所以，要先关）
                    return;
                } else {
                    if (textLine.isEmpty()) {
                        if (data.length() > 0) {
                            String dataStr = data.toString();
                            boolean isDone = (dataStr.length() == 6 && dataStr.equals("[DONE]"));

                            if (isDone) {
                                //可能会再次触发信号（所以，要先关）
                                RunUtil.runAndTry(reader::close);
                            }

                            subscriber.onNext(new ServerSentEvent(meta, dataStr)); //可能会触发再次进来（`subscription.request(1L)`）
                            l--; //提交后再减
                            meta.clear();
                            data.setLength(0);

                            if (isDone) {
                                subscriber.onComplete();
                                return;
                            }
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
        } catch (Throwable err) {
            RunUtil.runAndTry(reader::close);
            subscriber.onError(err);
        }
    }
}