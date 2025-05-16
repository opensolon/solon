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
import org.noear.solon.core.util.*;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.AbstractHttpUtils;
import org.noear.solon.net.http.impl.HttpSsl;
import org.noear.solon.net.http.impl.HttpUploadFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Http 工具 JDK HttpURLConnection 实现
 *
 * @author noear
 * @since 3.0
 */
public class JdkHttpUtils extends AbstractHttpUtils implements HttpUtils {
    static final Set<String> METHODS_NOBODY;

    static {
        METHODS_NOBODY = new HashSet<>(3); //es-GET 会有 body
        METHODS_NOBODY.add("HEAD");
        METHODS_NOBODY.add("TRACE");
        METHODS_NOBODY.add("OPTIONS");
    }

    public JdkHttpUtils(String url) {
        super(url);
    }

    @Override
    protected HttpResponse execDo(String _method, CompletableFuture<HttpResponse> future) throws IOException {
        final String method = _method.toUpperCase();
        final String newUrl = urlRebuild(method, _url, _charset);

        HttpURLConnection _builder = openConnection(newUrl);

        _builder.setUseCaches(false);

        if (_builder instanceof HttpsURLConnection) {
            //调整 ssl
            HttpsURLConnection tmp = ((HttpsURLConnection) _builder);
            tmp.setSSLSocketFactory(HttpSsl.getSSLSocketFactory());
            tmp.setHostnameVerifier(HttpSsl.defaultHostnameVerifier);
        }

        if (_timeout != null) {
            //调整 timeout
            if (_timeout.getConnectTimeout() != null) {
                _builder.setConnectTimeout((int) _timeout.getConnectTimeout().toMillis());
            }

            if (_timeout.getReadTimeout() != null) {
                _builder.setReadTimeout((int) _timeout.getReadTimeout().toMillis());
            }
        }

        if (_headers != null) {
            for (KeyValues<String> kv : _headers) {
                for (String val : kv.getValues()) {
                    _builder.addRequestProperty(kv.getKey(), val);
                }
            }
        }

        if (_cookies != null) {
            _builder.setRequestProperty("Cookie", getRequestCookieString(_cookies));
        }

        _builder.setRequestMethod(method);
        _builder.setDoInput(true);

        if (future == null) {
            return request(_builder, method);
        } else {
            RunUtil.async(() -> {
                try {
                    HttpResponse resp = request(_builder, method);
                    future.complete(resp);
                } catch (IOException | RuntimeException e) {
                    future.completeExceptionally(e);
                }
            });

            return null;
        }
    }

    private HttpURLConnection openConnection(String newUrl) throws IOException {
        if (_proxy == null) {
            return (HttpURLConnection) new URL(newUrl).openConnection();
        } else {
            return (HttpURLConnection) new URL(newUrl).openConnection(_proxy);
        }
    }

    private HttpResponse request(HttpURLConnection _builder, String method) throws IOException {
        try {
            if (METHODS_NOBODY.contains(method) == false) {
                if (_bodyRaw != null) {
                    if (_bodyRaw.getContentType() != null) {
                        _builder.setRequestProperty("Content-Type", _bodyRaw.getContentType());
                    }

                    _builder.setDoOutput(true);

                    try (OutputStream out = _builder.getOutputStream();
                         InputStream ins = _bodyRaw.getContent()) {
                        IoUtil.transferTo(ins, out);
                    }
                } else {
                    if (_multipart) {
                        _builder.setDoOutput(true);

                        new FormDataBody(_charset).write(_builder, _files, _params);
                    } else if (Utils.isEmpty(_params) == false) {
                        _builder.setDoOutput(true);

                        new FormBody(_charset).write(_builder, _params);
                    } else {
                        //HEAD 可以为空
                    }
                }
            }

            int statusCode = _builder.getResponseCode();

            if (isRedirected(statusCode)) {
                _url = _builder.getHeaderField("Location");
                return execDo(method, null);
            } else {
                return new JdkHttpResponse(this, statusCode, _builder);
            }
        } catch (IOException | RuntimeException e) {
            _builder.disconnect();
            throw e;
        }
    }

    public static class FormDataBody {
        private static final String CRLF = "\r\n";
        private static final String fileFormat = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"";
        private static final String textFormat = "Content-Disposition: form-data; name=\"%s\"";

        private final Charset charset;
        private final String contentType;
        private final String boundary;

        public FormDataBody(Charset charset) {
            this.boundary = Long.toHexString(System.currentTimeMillis());
            this.contentType = "multipart/form-data; boundary=" + boundary;
            this.charset = charset;
        }

        void write(HttpURLConnection http, MultiMap<HttpUploadFile> fileMap, MultiMap<String> paramMap) throws IOException {
            http.setRequestProperty("Content-Type", contentType);

            try (OutputStream out = http.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset), true)) {
                if (fileMap != null) {
                    for (KeyValues<HttpUploadFile> kv : fileMap) {
                        for (HttpUploadFile val : kv.getValues()) {
                            appendPartFile(out, writer, kv.getKey(), val);
                        }
                    }
                }

                if (paramMap != null) {
                    for (KeyValues<String> kv : paramMap) {
                        for (String val : kv.getValues()) {
                            appendPartText(out, writer, kv.getKey(), val);
                        }
                    }
                }

                writer.append("--").append(boundary).append("--").append(CRLF).flush();
            }
        }

        private void appendPartFile(OutputStream out, PrintWriter writer, String key, HttpUploadFile value) throws IOException {
            writer.append("--").append(boundary).append(CRLF);
            writer.append(String.format(fileFormat, HttpUtils.urlEncode(key, charset.name()), value.fileName)).append(CRLF);
            if (value.fileStream.getContentType() != null) {
                writer.append("Content-Type: ").append(value.fileStream.getContentType()).append(CRLF);
            }
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();

            try (InputStream ins = value.fileStream.getContent()) {
                IoUtil.transferTo(ins, out);
            }

            writer.append(CRLF).flush();
        }

        private void appendPartText(OutputStream out, PrintWriter writer, String key, String value) throws IOException {
            writer.append("--").append(boundary).append(CRLF);
            writer.append(String.format(textFormat, HttpUtils.urlEncode(key, charset.name()))).append(CRLF);
            writer.append(CRLF).flush();

            writer.append(value).flush();

            writer.append(CRLF).flush();
        }
    }

    public static class FormBody {
        private final String contentType;
        private final Charset charset;

        FormBody(Charset charset) {
            this.charset = charset;
            this.contentType = "application/x-www-form-urlencoded";
            //this.contentType = "application/x-www-form-urlencoded; charset=" + this.charset.name();
        }

        void write(HttpURLConnection http, MultiMap<String> paramMap) throws IOException {
            http.setRequestProperty("Content-Type", contentType);

            try (OutputStream out = http.getOutputStream()) {
                StringBuilder builder = new StringBuilder(128);
                for (KeyValues<String> kv : paramMap) {
                    // urlEncode : if charset is empty not do Encode
                    for (Object val : kv.getValues()) {
                        builder.append(HttpUtils.urlEncode(kv.getKey(), charset.name()));
                        builder.append("=");
                        builder.append(HttpUtils.urlEncode(String.valueOf(val), charset.name()));
                        builder.append("&");
                    }
                }

                String data = builder.delete(builder.length() - 1, builder.length()).toString();

                out.write(data.getBytes(charset));
                out.flush();
            }
        }
    }

    protected String urlRebuild(String method, String url, Charset charset) throws UnsupportedEncodingException {
        int pathOf = url.indexOf("://");
        int queryOf = url.indexOf("?");

        String schema = url.substring(0, pathOf + 3);
        String hostAndPath = queryOf > 0 ? url.substring(pathOf + 3, queryOf) : url.substring(pathOf + 3);
        String query = queryOf > 0 ? url.substring(queryOf) : "";

        if (hostAndPath.length() > 0) {
            String hostAndPath0 = URLDecoder.decode(hostAndPath, charset.name());

            if (hostAndPath.equals(hostAndPath0)) {
                hostAndPath = HttpUtils.urlEncode(hostAndPath, charset.name());
                hostAndPath = hostAndPath.replace("%2F", "/").replace("%3A", ":");
            }
        }

        if (query.length() > 0) {
            String query0 = URLDecoder.decode(query, charset.name());
            if (query.equals(query0)) {
                query = HttpUtils.urlEncode(query, charset.name());
                query = query.replace("%3F", "?")
                        .replace("%2F", "/")
                        .replace("%3A", ":")
                        .replace("%3D", "=")
                        .replace("%26", "&")
                        .replace("%40", "@")
                        .replace("%23", "#");
            }
        }

        StringBuilder newUrl = new StringBuilder();
        newUrl.append(schema);
        newUrl.append(hostAndPath);
        newUrl.append(query);

        if (_params != null && "GET".equals(method)) {
            for (KeyValues<String> kv : _params) {
                String key = HttpUtils.urlEncode(kv.getKey(), charset.name());
                for (String val : kv.getValues()) {
                    if (newUrl.indexOf("?") < 0) {
                        newUrl.append("?");
                    } else {
                        newUrl.append("&");
                    }
                    newUrl.append(key).append("=").append(HttpUtils.urlEncode(val, charset.name()));
                }
            }
            _params.clear();
        }

        return newUrl.toString();
    }

    /**
     * 允许 PATCH 方法
     */
    public static void allowPatch() {
        allowMethods("PATCH");
    }

    /**
     * 补丁，增加新方法支持
     */
    @SuppressWarnings("all")
    public static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            ClassUtil.accessibleAsTrue(modifiersField);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            ClassUtil.accessibleAsTrue(methodsField);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // do non thing
        }
    }
}