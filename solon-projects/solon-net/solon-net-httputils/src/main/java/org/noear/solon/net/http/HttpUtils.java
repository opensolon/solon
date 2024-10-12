/*
 * Copyright 2017-2024 noear.org and authors
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


import okhttp3.OkHttpClient;
import org.noear.solon.Solon;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsImpl;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Http 工具类
 *
 * @author noear
 * @since 2.8
 * */
public interface HttpUtils {
    static HttpUtils http(String service, String path) {
        String url = LoadBalanceUtils.getServer(null, service) + path;
        return http(url);
    }

    static HttpUtils http(String group, String service, String path) {
        String url = LoadBalanceUtils.getServer(group, service) + path;
        return http(url);
    }

    static HttpUtils http(String url) {
        if (ClassUtil.hasClass(() -> OkHttpClient.class)) {
            return new OkHttpUtilsImpl(url);
        } else {
            return new JdkHttpUtilsImpl(url);
        }
    }

    /**
     * 启用打印（专为 tester 服务）
     */
    HttpUtils enablePrintln(boolean enable);

    /**
     * 超时配置
     */
    HttpUtils timeout(int timeoutSeconds);

    /**
     * 超时配置
     */
    HttpUtils timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds);

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
    HttpUtils bodyTxt(String txt);

    /**
     * 主体配置
     */
    HttpUtils bodyTxt(String txt, String contentType);

    /**
     * 主体配置
     */
    HttpUtils bodyJson(String txt);

    /**
     * 主体配置
     */
    HttpUtils bodyRaw(byte[] bytes);

    /**
     * 主体配置
     */
    HttpUtils bodyRaw(byte[] bytes, String contentType);

    /**
     * 主体配置
     */
    HttpUtils bodyRaw(InputStream raw);

    /**
     * 主体配置
     */
    HttpUtils bodyRaw(InputStream raw, String contentType);


    /**
     * get 请求并返回 body
     */
    String get() throws IOException;

    /**
     * post 请求并返回 body
     */
    String post() throws IOException;

    /**
     * put 请求并返回 body
     */
    String put() throws IOException;

    /**
     * patch 请求并返回 body
     */
    String patch() throws IOException;

    /**
     * delete 请求并返回 body
     */
    String delete() throws IOException;

    /**
     * options 请求并返回 body
     */
    String options() throws IOException;

    /**
     * head 请求并返回 code
     */
    int head() throws IOException;

    //////

    /**
     * 执行请求并返回响应主体
     */
    String execAsBody(String mothod) throws IOException;

    /**
     * 执行请求并返回代码
     */
    int execAsCode(String method) throws IOException;

    /**
     * 执行请求并返回响应
     */
    HttpResponse exec(String method) throws IOException;

    /**
     * 异步执行请求
     */
    CompletableFuture<HttpResponse> execAsync(String method);


    //////

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
    static String toQueryString(Map<?, ?> map) throws IOException {
        return toQueryString(map, null);
    }

    /**
     * map 转为 queryString
     */
    static String toQueryString(Map<?, ?> map, String charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(String.format("%s=%s",
                    urlEncode(entry.getKey().toString(), charset),
                    urlEncode(entry.getValue().toString(), charset)
            ));
        }
        return sb.toString();
    }
}