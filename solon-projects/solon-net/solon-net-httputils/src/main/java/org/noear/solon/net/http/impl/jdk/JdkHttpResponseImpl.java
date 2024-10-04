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
package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.net.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Http 响应 JDK HttpURLConnection 实现
 *
 * @author noear
 * @since 3.0
 */
public class JdkHttpResponseImpl implements HttpResponse {
    private final HttpURLConnection http;
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final InputStream body;

    public JdkHttpResponseImpl(HttpURLConnection http) throws IOException {
        this.http = http;

        this.statusCode = http.getResponseCode();
        this.headers = http.getHeaderFields();

        InputStream inputStream = statusCode < 400 ? http.getInputStream() : http.getErrorStream();
        // 获取响应头是否有Content-Encoding=gzip
        String gzip = http.getHeaderField("Content-Encoding");
        if(Utils.isNotEmpty(gzip) && gzip.contains("gzip")) {
            inputStream = new GZIPInputStream(inputStream);
        }
        body = new JdkInputStreamWrapper(http, inputStream);
    }

    @Override
    public Collection<String> headerNames() {
        return headers.keySet();
    }

    @Override
    public String header(String name) {
        List<String> values = headers(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    @Override
    public List<String> headers(String name) {
        return headers.get(name);
    }

    @Override
    public Long contentLength() {
        return http.getContentLengthLong();
    }

    @Override
    public String contentType() {
        return http.getContentType();
    }

    @Override
    public Charset contentEncoding() {
        String tmp = http.getContentEncoding();
        return tmp == null ? null : Charset.forName(tmp);
    }

    @Override
    public List<String> cookies() {
        return headers("Set-Cookie");
    }

    @Override
    public int code() {
        return statusCode;
    }

    @Override
    public InputStream body() {
        return body;
    }

    @Override
    public byte[] bodyAsBytes() throws IOException {
        try {
            return IoUtil.transferToBytes(body());
        } finally {
            body().close();
        }
    }

    @Override
    public String bodyAsString() throws IOException {
        try {
            return IoUtil.transferToString(body());
        } finally {
            body().close();
        }
    }
}
