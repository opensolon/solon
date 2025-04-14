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
package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.exception.SolonException;
import org.noear.solon.net.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Http 响应 JDK HttpURLConnection 实现
 *
 * @author noear
 * @since 3.0
 */
public class JdkHttpResponse implements HttpResponse {
    private final JdkHttpUtils utils;
    private final HttpURLConnection http;
    private final int statusCode;
    private final MultiMap<String> headers;
    private MultiMap<String> cookies;
    private final InputStream body;

    public JdkHttpResponse(JdkHttpUtils utils, HttpURLConnection http) throws IOException {
        this.utils = utils;
        this.http = http;

        this.statusCode = http.getResponseCode();
        this.headers = new MultiMap<>();

        for (Map.Entry<String, List<String>> kv : http.getHeaderFields().entrySet()) {
            if (kv.getKey() != null) {
                headers.holder(kv.getKey()).setValues(kv.getValue());
            }
        }

        InputStream inputStream = statusCode < 400 ? http.getInputStream() : http.getErrorStream();

        if (null == inputStream) {
            //当流为 null 时（给个空的）
            body = new ByteArrayInputStream("".getBytes());
        } else {
            // 获取响应头是否有 Content-Encoding
            String encoding = http.getHeaderField("Content-Encoding");

            if (Utils.isNotEmpty(encoding)) {
                if ("gzip".equalsIgnoreCase(encoding)) {
                    if (inputStream instanceof GZIPInputStream == false) {
                        inputStream = new GZIPInputStream(inputStream);
                    }
                } else if ("deflate".equalsIgnoreCase(encoding)) {
                    if (inputStream instanceof InflaterInputStream == false) {
                        inputStream = new InflaterInputStream(inputStream, new Inflater(true));
                    }
                }
            }

            body = new JdkInputStreamWrapper(http, inputStream);
        }
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
        return headers.getAll(name);
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
    public void close() throws IOException {
        body().close();
    }
}