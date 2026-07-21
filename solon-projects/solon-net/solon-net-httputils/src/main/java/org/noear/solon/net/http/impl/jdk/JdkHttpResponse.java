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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.exception.SolonException;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpResponseException;

import java.io.BufferedInputStream;
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
    private final String statusMessage;
    private final MultiMap<String> headers;
    private volatile MultiMap<String> cookies;
    private final InputStream body;

    public JdkHttpResponse(JdkHttpUtils utils, int statusCode, HttpURLConnection http) throws IOException {
        this.utils = utils;
        this.http = http;

        this.statusCode = statusCode;
        this.statusMessage = http.getResponseMessage();
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
                // 用 BufferedInputStream 包装以便 GZIP/deflate 失败时可 reset 回退
                if (!(inputStream instanceof BufferedInputStream)) {
                    inputStream = new BufferedInputStream(inputStream, 8192);
                }
                inputStream.mark(8192);

                try {
                    if ("gzip".equalsIgnoreCase(encoding)) {
                        if (inputStream instanceof GZIPInputStream == false) {
                            inputStream = new GZIPInputStream(inputStream);
                        }
                    } else if ("deflate".equalsIgnoreCase(encoding)) {
                        if (inputStream instanceof InflaterInputStream == false) {
                            inputStream = new InflaterInputStream(inputStream, new Inflater(true));
                        }
                    }
                } catch (IOException e) {
                    // Content-Encoding 标注了压缩但实际 body 未压缩，
                    // reset 到标记位置，使用原始未解压流
                    try {
                        inputStream.reset();
                    } catch (IOException ignored) {
                        // reset 失败说明流已损坏，使用空流兜底
                        inputStream = new ByteArrayInputStream(new byte[0]);
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
                // 防御：如果 Set-Cookie 值中不含 '='，跳过该 cookie 避免异常
                if (eqIdx < 0) {
                    continue;
                }
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


    private volatile Charset _contentCharset;
    @Override
    public Charset contentCharset() {
        if (_contentCharset == null) {
            _contentCharset = parseContentCharset(contentType());
        }

        return _contentCharset;
    }

    public static Charset parseContentCharset(String contentType) {
        //用于单测
        if (contentType != null) {
            int charsetIdx = contentType.indexOf("charset=");
            if (charsetIdx > 0) {
                String charset = contentType.substring(charsetIdx + 8);
                if (charset.indexOf(';') > 0) {
                    charset = charset.substring(0, charset.indexOf(';'));
                }
                try {
                    return Charset.forName(charset.trim());
                } catch (Exception e) {
                    // 非法或未知的 charset 名称，返回 null 让调用方回退到默认字符集
                    return null;
                }
            }
        }

        return null;
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
    public String message() {
        return statusMessage;
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
            // 优先使用 Content-Type 中的 charset，其次使用客户端配置的 charset，最后使用系统默认编码
            // 注意：Content-Encoding（gzip/deflate）已在构造器中解压，此处不应作为字符集使用
            Charset charset = contentCharset();
            if (charset == null) {
                if (utils.charset() != null) {
                    charset = utils.charset();
                } else {
                    charset = Charset.forName(Solon.encoding());
                }
            }
            return IoUtil.transferToString(body(), charset.name());
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
    public HttpResponseException createError() {
        return new HttpResponseException(this, http.getRequestMethod(), http.getURL());
    }

    private volatile Map<String, List<String>> headerMap;
    @Override
    public Map<String, List<String>> headerMap() {
        if (headerMap == null) {
            headerMap = new LinkedHashMap<>();
            for (String name : headerNames()) {
                headerMap.put(name, headers(name));
            }
        }

        return headerMap;
    }

    @Override
    public void close() throws IOException {
        body().close();
    }
}