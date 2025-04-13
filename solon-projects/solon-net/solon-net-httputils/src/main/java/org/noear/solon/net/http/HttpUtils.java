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
package org.noear.solon.net.http;


import org.noear.solon.Solon;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.lang.Preview;
import org.noear.solon.net.http.textstream.ServerSentEvent;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Http 工具类
 *
 * @author noear
 * @since 2.8
 * */
@Preview("3.0")
public interface HttpUtils {
    static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 创建
     */
    static HttpUtils http(String service, String path) {
        String url = LoadBalanceUtils.getServer(null, service) + path;
        return http(url);
    }

    /**
     * 创建
     */
    static HttpUtils http(String group, String service, String path) {
        String url = LoadBalanceUtils.getServer(group, service) + path;
        return http(url);
    }

    /**
     * 创建
     */
    static HttpUtils http(String url) {
        return HttpConfiguration.getFactory().http(url);
    }

    /**
     * 配置序列化器
     */
    HttpUtils serializer(Serializer serializer);

    /**
     * 获取序列化器
     */
    Serializer serializer();

    /**
     * 启用打印（专为 tester 服务）
     */
    HttpUtils enablePrintln(boolean enable);

    /**
     * 代理配置
     */
    HttpUtils proxy(Proxy proxy);

    /**
     * 代理配置
     */
    default HttpUtils proxy(String host, int port) {
        return proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
    }

    /**
     * 超时配置
     */
    HttpUtils timeout(int timeoutSeconds);

    /**
     * 超时配置
     */
    HttpUtils timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds);

    /**
     * 超时配置
     */
    default HttpUtils timeout(HttpTimeout timeout) {
        if (timeout != null) {
            return timeout(timeout.connectTimeout, timeout.writeTimeout, timeout.readTimeout);
        }
        return this;
    }

    /**
     * 是否多部分配置
     */
    HttpUtils multipart(boolean multipart);

    /**
     * 用户代理配置
     */
    HttpUtils userAgent(String ua);

    /**
     * 编码配置
     */
    HttpUtils charset(String charset);

    /**
     * 头配置
     */
    HttpUtils headers(Map headers);

    /**
     * 头配置
     */
    HttpUtils headers(Iterable<KeyValues<String>> headers);

    /**
     * 头配置（替换）
     */
    HttpUtils header(String name, String value);

    /**
     * 头配置（添加）
     */
    HttpUtils headerAdd(String name, String value);


    /**
     * Content-Type 头配置
     */
    default HttpUtils contentType(String contentType) {
        return header("Content-Type", contentType);
    }

    /**
     * Accept 头配置
     */
    default HttpUtils accept(String accept) {
        return header("Accept", accept);
    }


    /**
     * 小饼配置
     */
    HttpUtils cookies(Map cookies);

    /**
     * 小饼配置
     */
    HttpUtils cookies(Iterable<KeyValues<String>> cookies);

    /**
     * 小饼配置（替换）
     */
    HttpUtils cookie(String name, String value);

    /**
     * 小饼配置（添加）
     */
    HttpUtils cookieAdd(String name, String value);

    /**
     * 参数配置
     */
    HttpUtils data(Map data);

    /**
     * 参数配置
     */
    HttpUtils data(Iterable<KeyValues<String>> data);

    /**
     * 参数配置（替换）
     */
    HttpUtils data(String name, String value);

    /**
     * 参数配置
     */
    HttpUtils data(String name, String filename, InputStream inputStream, String contentType);

    /**
     * 参数配置
     */
    HttpUtils data(String name, String filename, File file);

    /**
     * 参数配置
     */
    default HttpUtils data(String name, File file) {
        return data(name, file.getName(), file);
    }

    /**
     * 主体配置
     */
    default HttpUtils bodyOfTxt(String txt) {
        return body(txt, "text/plain");
    }

    /**
     * 主体配置
     */
    default HttpUtils bodyOfJson(String txt) {
        return body(txt, "application/json");
    }

    /**
     * 主体配置（由序列化器决定内容类型）
     */
    HttpUtils bodyOfBean(Object obj) throws HttpException;

    /**
     * 主体配置
     */
    HttpUtils body(String txt, String contentType);

    /**
     * 主体配置
     */
    HttpUtils body(byte[] bytes, String contentType);

    /**
     * 主体配置
     */
    default HttpUtils body(byte[] bytes) {
        return body(bytes, null);
    }

    /**
     * 主体配置
     */
    HttpUtils body(InputStream raw, String contentType);

    /**
     * 主体配置
     */
    default HttpUtils body(InputStream raw) {
        return body(raw, null);
    }


    /**
     * get 请求并返回 body
     */
    String get() throws HttpException;

    /**
     * get 请求并返回 body
     */
    <T> T getAs(Type type) throws HttpException;

    /**
     * post 请求并返回 body
     */
    String post() throws HttpException;

    /**
     * post 请求并返回 body
     */
    <T> T postAs(Type type) throws HttpException;

    /**
     * post 请求并返回 body
     */
    default String post(boolean useMultipart) throws HttpException {
        if (useMultipart) {
            multipart(true);
        }

        return post();
    }

    /**
     * post 请求并返回 body
     */
    default <T> T postAs(Type type, boolean useMultipart) throws HttpException {
        if (useMultipart) {
            multipart(true);
        }

        return postAs(type);
    }

    /**
     * put 请求并返回 body
     */
    String put() throws HttpException;

    /**
     * put 请求并返回 body
     */
    <T> T putAs(Type type) throws HttpException;

    /**
     * patch 请求并返回 body
     */
    String patch() throws HttpException;

    /**
     * patch 请求并返回 body
     */
    <T> T patchAs(Type type) throws HttpException;

    /**
     * delete 请求并返回 body
     */
    String delete() throws HttpException;

    /**
     * delete 请求并返回 body
     */
    <T> T deleteAs(Type type) throws HttpException;


    /**
     * options 请求并返回 body
     */
    String options() throws HttpException;

    /**
     * head 请求并返回 code
     */
    int head() throws HttpException;

    //////

    /**
     * 执行请求并返回响应主体
     */
    String execAsBody(String method) throws HttpException;

    /**
     * 执行请求并返回响应主体
     */
    <T> T execAsBody(String method, Type type) throws HttpException;

    /**
     * 执行请求并返回代码
     */
    int execAsCode(String method) throws HttpException;

    /**
     * 执行请求并返回文本行流
     */
    Publisher<String> execAsLineStream(String method);

    /**
     * 执行请求并返回文本流
     *
     * @deprecated 3.1 {@link #execAsLineStream(String)}
     */
    @Deprecated
    default Publisher<String> execAsTextStream(String method) {
        return execAsLineStream(method);
    }


    /**
     * 执行请求并返回服务端推送事件流
     */
    Publisher<ServerSentEvent> execAsSseStream(String method);

    /**
     * 执行请求并返回服务端推送事件流
     *
     * @deprecated 3.1 {@link #execAsSseStream(String)}
     */
    @Deprecated
    default Publisher<ServerSentEvent> execAsEventStream(String method) {
        return execAsSseStream(method);
    }

    /**
     * 执行请求并返回响应
     */
    HttpResponse exec(String method) throws HttpException;

    /**
     * 异步执行请求
     */
    CompletableFuture<HttpResponse> execAsync(String method);


    /////////////

    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #body(String, String)}
     */
    @Deprecated
    default HttpUtils bodyTxt(String txt, String contentType) {
        log.warn("'HttpUtils.bodyTxt(.,.)' will be removed, please use 'HttpUtils.body(.,.)'!");
        return body(txt, contentType);
    }

    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #bodyOfTxt(String)}
     */
    @Deprecated
    default HttpUtils bodyTxt(String txt) {
        log.warn("'HttpUtils.bodyTxt(.)' will be removed, please use 'HttpUtils.bodyOfTxt(.)'!");
        return bodyOfTxt(txt);
    }

    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #bodyOfJson(String)}
     */
    @Deprecated
    default HttpUtils bodyJson(String txt) {
        log.warn("'HttpUtils.bodyJson(.)' will be removed, please use 'HttpUtils.bodyOfJson(.)'!");
        return bodyOfJson(txt);
    }


    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #body(byte[], String)}
     */
    @Deprecated
    default HttpUtils bodyRaw(byte[] bytes, String contentType) {
        log.warn("'HttpUtils.bodyRaw(.,.)' will be removed, please use 'HttpUtils.body(.,.)'!");
        return body(bytes, contentType);
    }

    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #body(byte[])}
     */
    @Deprecated
    default HttpUtils bodyRaw(byte[] bytes) {
        log.warn("'HttpUtils.bodyRaw(.)' will be removed, please use 'HttpUtils.body(.)'!");
        return body(bytes);
    }

    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #body(InputStream)}
     */
    @Deprecated
    default HttpUtils bodyRaw(InputStream raw) {
        log.warn("'HttpUtils.bodyRaw(.)' will be removed, please use 'HttpUtils.body(.)'!");
        return body(raw);
    }

    /**
     * 主体配置
     *
     * @deprecated 3.0 {@link #body(byte[], String)}
     */
    @Deprecated
    default HttpUtils bodyRaw(InputStream raw, String contentType) {
        log.warn("'HttpUtils.bodyRaw(.,.)' will be removed, please use 'HttpUtils.body(.,.)'!");
        return body(raw, contentType);
    }


    /////////////

    /**
     * url 编码
     */
    static String urlEncode(String s) throws IOException {
        return urlEncode(s, null);
    }

    /**
     * url 编码
     */
    static String urlEncode(String s, String charset) throws UnsupportedEncodingException {
        if (charset == null) {
            return URLEncoder.encode(s, Solon.encoding());
        } else {
            return URLEncoder.encode(s, charset);
        }
    }

    /**
     * map 转为 queryString
     */
    static CharSequence toQueryString(Map<?, ?> map) throws IOException {
        return toQueryString(map, null);
    }

    /**
     * map 转为 queryString
     */
    static CharSequence toQueryString(Map<?, ?> map, String charset) throws IOException {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (buf.length() > 0) {
                buf.append('&');
            }

            buf.append(urlEncode(entry.getKey().toString(), charset))
                    .append('=')
                    .append(urlEncode(entry.getValue().toString(), charset));
        }

        return buf.toString();
    }
}