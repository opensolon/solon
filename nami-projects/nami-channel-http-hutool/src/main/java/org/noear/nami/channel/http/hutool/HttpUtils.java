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
package org.noear.nami.channel.http.hutool;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import org.noear.nami.NamiException;
import org.noear.nami.NamiGlobal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Http 处理工具
 * */
class HttpUtils {

    public static HttpUtils http(String url){
        return new HttpUtils(url);
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new NamiException(ex);
        }
    }

    private HttpRequest _builder;
    public HttpUtils(String url) {
        _builder = new HttpRequest(UrlBuilder.ofHttp(url));

        _builder.setConnectionTimeout(NamiGlobal.getConnectTimeout() * 1000);
        _builder.setReadTimeout(NamiGlobal.getReadTimeout() * 1000);
    }


    /**
     * 设置charset
     * */
    public HttpUtils charset(String charset){
        _builder.charset(charset);
        return this;
    }

    /**
     * 设置请求头
     * */
    public HttpUtils headers(Map<String,String> headers) {
        if (headers != null) {
            headers.forEach((k, v) -> {
                if (v != null) {
                    _builder.header(k, v);
                }
            });
        }

        return this;
    }


    /**
     * 设置表单数据
     * */
    public HttpUtils data(Map<String,Object> data) {
        if (data != null) {
            _builder.form(data);
        }

        return this;
    }

    /**
     * 设置BODY数据
     * */
    public HttpUtils bodyRaw(byte[] bytes, String contentType) {
        _builder.body(bytes).contentType(contentType);
        return this;
    }

    /**
     * 超时设置
     * */
    public HttpUtils timeout(int seconds) {
        if (seconds > 0) {
            _builder.timeout(seconds * 1000);
        }

        return this;
    }


    /**
     * 执行请求，返回响应对象
     * */
    public HttpResponse exec(String mothod) throws Exception {
        switch (mothod.toUpperCase()){
            case "GET":_builder.method(Method.GET);break;
            case "POST":_builder.method(Method.POST);break;
            case "PUT":_builder.method(Method.PUT);break;
            case "DELETE":_builder.method(Method.DELETE);break;
            case "PATCH":_builder.method(Method.PATCH);break;
            case "OPTIONS":_builder.method(Method.OPTIONS);break;
            default: throw new IllegalStateException("This method is not supported");
        }

        return  _builder.execute();
    }
}