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
package org.noear.solon.net.http.impl.okhttp;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.exception.SolonException;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * Http 响应 OkHttp 实现
 *
 * @author noear
 * @since 2.8
 */
public class OkHttpResponse implements HttpResponse {
    private final OkHttpUtils utils;
    private final Response response;
    private MultiMap<String> cookies;
    private final int statusCode;
    private final String statusMessage;

    public OkHttpResponse(OkHttpUtils utils, int statusCode, Response response) {
        this.utils = utils;
        this.response = response;
        this.statusCode = statusCode;
        this.statusMessage = response.message();
    }

    private MultiMap<String> cookiesInit() {
        if (cookies == null) {
            cookies = new MultiMap<>();

            List<String> kvAry = headers("Set-Cookie");
            for (String kvStr : kvAry) {
                int eqIdx = kvStr.indexOf("=");
                int smIdx = kvStr.indexOf(";", eqIdx);

                String key = kvStr.substring(0, eqIdx);
                String value = smIdx > 0 ? kvStr.substring(eqIdx + 1, smIdx) : kvStr.substring(eqIdx + 1);

                cookies.add(key, value);
            }
        }

        return cookies;
    }

    @Override
    public Collection<String> headerNames() {
        return response.headers().names();
    }

    @Override
    public String header(String name) {
        return response.header(name);
    }

    @Override
    public List<String> headers(String name) {
        return response.headers(name);
    }

    @Override
    public Collection<String> cookieNames() {
        return cookiesInit().keySet();
    }

    @Override
    public String cookie(String name) {
        return cookiesInit().get(name);
    }

    @Override
    public List<String> cookies(String name) {
        return cookiesInit().getAll(name);
    }

    @Override
    public Long contentLength() {
        return response.body().contentLength();
    }

    @Override
    public String contentType() {
        MediaType tmp = response.body().contentType();
        if (tmp == null) {
            return null;
        } else {
            return tmp.toString();
        }
    }

    @Override
    public Charset contentCharset() {
        MediaType tmp = response.body().contentType();
        if (tmp == null) {
            return null;
        } else {
            return tmp.charset();
        }
    }

    @Override
    public List<String> cookies() {
        return response.headers("Set-Cookie");
    }

    @Override
    public int code() {
        return statusCode;
    }

    @Override
    public String message() {
        return statusMessage;
    }

    @Override
    public InputStream body() {
        return response.body().byteStream();
    }

    @Override
    public byte[] bodyAsBytes() throws IOException {
        return response.body().bytes();
    }

    @Override
    public String bodyAsString() throws IOException {
        return response.body().string();
    }

    @Override
    public <T> T bodyAsBean(Type type) throws IOException {
        if (String.class == utils.serializer().dataType()) {
            return (T) utils.serializer().deserialize(bodyAsString(), type);
        } else if (byte[].class == utils.serializer().dataType()) {
            return (T) utils.serializer().deserialize(bodyAsBytes(), type);
        } else {
            throw new SolonException("Invalid serializer type!");
        }
    }

    @Override
    public HttpResponseException createError() {
        return new HttpResponseException(this, response.request().method(), response.request().url().url());
    }

    @Override
    public void close() throws IOException {
        response.close();
    }
}
