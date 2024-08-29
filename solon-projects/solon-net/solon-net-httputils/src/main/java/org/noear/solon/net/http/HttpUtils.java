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


import org.noear.solon.Solon;
import org.noear.solon.net.http.impl.HttpUtilsImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

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
        return new HttpUtilsImpl(url);
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
    HttpUtils headers(Map<String, String> headers);

    /**
     * 头配置（替换）
     */
    HttpUtils header(String name, String value);

    /**
     * 头配置（添加）
     */
    HttpUtils headerAdd(String name, String value);

    /**
     * 参数配置
     */
    HttpUtils data(Map data);

    /**
     * 参数配置
     */
    HttpUtils data(String key, String value);

    /**
     * 参数配置
     */
    HttpUtils data(String key, String filename, InputStream inputStream, String contentType);

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
     * 主体配置
     */
    HttpUtils cookies(Map<String, String> cookies);


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
     * 执行请求并返回响应
     */
    HttpResponse exec(String mothod) throws IOException;

    /**
     * 执行请求并返回响应主体
     */
    String execAsBody(String mothod) throws IOException;

    /**
     * 执行请求并返回代码
     */
    int execAsCode(String mothod) throws IOException;

    //////

    /**
     * post 异步执行请求
     */
    void postAsync(HttpCallback callback) throws IOException;

    /**
     * get 异步执行请求
     */
    void getAsync(HttpCallback callback) throws IOException;

    /**
     * head 异步执行请求
     */
    void headAsync(HttpCallback callback) throws IOException;

    /**
     * 异步执行请求
     */
    void execAsync(String method, HttpCallback callback);


    /**
     * url 编码
     */
    static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, Solon.encoding());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * map 转为 queryString
     */
    static String toQueryString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncode(entry.getKey().toString()),
                    urlEncode(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }
}