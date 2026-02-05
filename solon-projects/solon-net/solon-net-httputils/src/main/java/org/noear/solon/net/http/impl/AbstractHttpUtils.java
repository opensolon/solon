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
package org.noear.solon.net.http.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.http.textstream.ServerSentEvent;
import org.noear.solon.net.http.textstream.TextStreamUtil;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.exception.SolonException;
import org.noear.solon.net.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Http 工具基类
 *
 * @author noear
 * @since 3.0
 */
public abstract class AbstractHttpUtils implements HttpUtils {
    static final Logger log = LoggerFactory.getLogger(AbstractHttpUtils.class);

    protected boolean _enablePrintln = false;

    protected Proxy _proxy = null;
    protected HttpSslSupplier _sslSupplier = null;

    protected String _url;
    protected Charset _charset = StandardCharsets.UTF_8;
    protected MultiMap<String> _headers;
    protected MultiMap<String> _cookies;
    protected MultiMap<String> _params;
    protected MultiMap<HttpUploadFile> _files;

    protected boolean _multipart = false;
    protected HttpTimeout _timeout;
    protected Serializer _serializer;

    public AbstractHttpUtils(String url) {
        _url = url;
        _timeout = HttpTimeout.of(10, 60, 60); //默认超时 60s

        if (url.contains("://") == false) {
            throw new IllegalArgumentException("No url scheme 'http' or 'https' found: " + url);
        }

        initExtension();
    }

    @Override
    public HttpUtils serializer(Serializer serializer) {
        if (serializer != null) {
            if (serializer.mimeType() == null) {
                throw new IllegalArgumentException("Invalid Serializer mimeType: " + serializer.getClass().getName());
            }

            if (serializer.dataType() == null) {
                throw new IllegalArgumentException("Invalid Serializer dataType: " + serializer.getClass().getName());
            }

            _serializer = serializer;
        }
        return this;
    }

    @Override
    public Serializer serializer() {
        if (_serializer == null) {
            if (Solon.app() != null) {
                _serializer = Solon.app().serializers().get(SerializerNames.AT_JSON);
            }

            if (_serializer == null) {
                throw new SolonException("Missing serializer!");
            }
        }

        return _serializer;
    }

    /**
     * 初始化扩展
     */
    private void initExtension() {
        for (HttpExtension ext : HttpConfiguration.getExtensions()) {
            ext.onInit(this, _url);
        }
    }

    @Override
    public HttpUtils enablePrintln(boolean enable) {
        _enablePrintln = enable;
        return this;
    }

    @Override
    public HttpUtils proxy(Proxy proxy) {
        _proxy = proxy;
        return this;
    }

    @Override
    public HttpUtils ssl(HttpSslSupplier sslProvider) {
        _sslSupplier = sslProvider;
        return this;
    }

    @Override
    public HttpUtils timeout(HttpTimeout timeout) {
        if (timeout != null) {
            _timeout = timeout;
        }
        return this;
    }

    @Override
    public HttpUtils multipart(boolean multipart) {
        _multipart = multipart;
        return this;
    }

    @Override
    public HttpUtils userAgent(String ua) {
        header("User-Agent", ua);
        return this;
    }

    @Override
    public HttpUtils charset(String charset) {
        _charset = Charset.forName(charset);
        return this;
    }

    public Charset charset() {
        return _charset;
    }

    /**
     * 设置请求头
     */
    @Override
    public HttpUtils headers(Map headers) {
        if (headers != null) {
            tryInitHeaders();
            headers.forEach((k, v) -> {
                if (k != null && v != null) {
                    _headers.put(k.toString(), v.toString());
                }
            });
        }

        return this;
    }

    @Override
    public HttpUtils headers(Iterable<KeyValues<String>> headers) {
        if (headers != null) {
            tryInitHeaders();
            for (KeyValues<String> kv : headers) {
                _headers.holder(kv.getKey()).setValues(kv.getValues());
            }
        }

        return this;
    }

    /**
     * 设置请求头
     */
    @Override
    public HttpUtils header(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        tryInitHeaders().put(name, value);
        return this;
    }

    /**
     * 添加请求头
     */
    @Override
    public HttpUtils headerAdd(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        tryInitHeaders().add(name, value);
        return this;
    }

    /**
     * 设置请求 cookies
     */
    @Override
    public HttpUtils cookies(Map cookies) {
        if (cookies != null) {
            tryInitCookies();
            cookies.forEach((k, v) -> {
                if (k != null && v != null) {
                    _cookies.put(k.toString(), v.toString());
                }
            });
        }

        return this;
    }

    @Override
    public HttpUtils cookies(Iterable<KeyValues<String>> cookies) {
        if (cookies != null) {
            tryInitCookies();
            for (KeyValues<String> kv : cookies) {
                _cookies.holder(kv.getKey()).setValues(kv.getValues());
            }
        }

        return this;
    }

    @Override
    public HttpUtils cookie(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        tryInitCookies().put(name, value);
        return this;
    }

    @Override
    public HttpUtils cookieAdd(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        tryInitCookies().add(name, value);
        return this;
    }

    /**
     * 设置表单数据
     */
    @Override
    public HttpUtils data(Map data) {
        if (data != null) {
            tryInitParams();
            data.forEach((k, v) -> {
                if (k != null && v != null) {
                    _params.put(k.toString(), v.toString());
                }
            });
        }

        return this;
    }

    @Override
    public HttpUtils data(Iterable<KeyValues<String>> data) {
        if (data != null) {
            tryInitParams();
            for (KeyValues<String> kv : data) {
                _params.holder(kv.getKey()).setValues(kv.getValues());
            }
        }

        return this;
    }

    /**
     * 设置表单数据
     */
    @Override
    public HttpUtils data(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        tryInitParams().add(name, value);
        return this;
    }

    @Override
    public HttpUtils data(String name, String filename, InputStream inputStream, String contentType) {
        if (name == null || inputStream == null) {
            return this;
        }

        multipart(true);
        tryInitFiles().add(name, new HttpUploadFile(filename, new HttpStream(inputStream, contentType)));

        return this;
    }

    @Override
    public HttpUtils data(String name, String filename, File file) {
        if (name == null || file == null) {
            return this;
        }

        if (filename == null) {
            filename = file.getName();
        }

        multipart(true);
        tryInitFiles().add(name, new HttpUploadFile(filename, new HttpStream(filename, file)));

        return this;
    }



    @Override
    public String get() throws HttpException {
        return execAsBody("GET");
    }

    @Override
    public <T> T getAs(Type type) throws HttpException {
        return execAsBody("GET", type);
    }

    @Override
    public String post() throws HttpException {
        return execAsBody("POST");
    }

    @Override
    public <T> T postAs(Type type) throws HttpException {
        return execAsBody("POST", type);
    }

    @Override
    public String put() throws HttpException {
        return execAsBody("PUT");
    }

    @Override
    public <T> T putAs(Type type) throws HttpException {
        return execAsBody("PUT", type);
    }

    @Override
    public String patch() throws HttpException {
        return execAsBody("PATCH");
    }

    @Override
    public <T> T patchAs(Type type) throws HttpException {
        return execAsBody("PATCH", type);
    }

    @Override
    public String delete() throws HttpException {
        return execAsBody("DELETE");
    }

    @Override
    public <T> T deleteAs(Type type) throws HttpException {
        return execAsBody("DELETE", type);
    }

    @Override
    public String options() throws HttpException {
        return execAsBody("OPTIONS");
    }

    @Override
    public int head() throws HttpException {
        return execAsCode("HEAD");
    }


    @Override
    public String execAsBody(String method) throws HttpException {
        try {
            try (HttpResponse resp = exec(method)) {
                String text = resp.bodyAsString();

                if (_enablePrintln) {
                    System.out.println(method + " " + _url + ":: " + text);
                }

                return text;
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new SolonException(e);
        }
    }

    @Override
    public <T> T execAsBody(String method, Type type) throws HttpException {
        try {
            try (HttpResponse resp = exec(method)) {
                return resp.bodyAsBean(type);
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new SolonException(e);
        }
    }

    @Override
    public int execAsCode(String method) throws HttpException {
        try {
            try (HttpResponse resp = exec(method)) {
                int code = resp.code();

                if (_enablePrintln) {
                    System.out.println(method + " " + _url + "::code:: " + code);
                }

                return code;
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new SolonException(e);
        }
    }

    @Override
    public Flux<String> execAsLineStream(String method) {
        return Mono.fromFuture(execAsync(method))
                .flatMapMany(resp -> {
                    if (resp.code() < 400) {
                        // 直接使用非弃用的 Flux 返回接口
                        return TextStreamUtil.parseLineStream(resp);
                    } else {
                        // 保持原有的错误构造逻辑
                        return Flux.error(createHttpException(resp));
                    }
                });
    }

    @Override
    public Flux<ServerSentEvent> execAsSseStream(String method) {
        this.header("Accept", "text/event-stream");
        this.header("Cache-Control", "no-cache");

        return Mono.fromFuture(execAsync(method))
                .flatMapMany(resp -> {
                    if (resp.code() < 400) {
                        // 直接使用非弃用的 Flux 返回接口
                        return TextStreamUtil.parseSseStream(resp);
                    } else {
                        // 保持原有的错误构造逻辑
                        return Flux.error(createHttpException(resp));
                    }
                });
    }

    /**
     * 提取公共异常构造逻辑（保持原逻辑：尝试读取 body 报错）
     */
    private HttpException createHttpException(HttpResponse resp) {
        String message = RunUtil.callAndTry(resp::bodyAsString);
        if (Utils.isEmpty(message)) {
            return new HttpException("Error code: " + resp.code());
        } else {
            return new HttpException("Error code: " + resp.code() + ", message: " + message);
        }
    }

    /**
     * 执行请求，返回响应对象（需要自己做关闭处理）
     */
    @Override
    public HttpResponse exec(String method) throws HttpException {
        try {
            return execDo(method, null);
        } catch (Exception e) {
            throw new HttpException(method + " " + _url + ", request failed", e);
        }
    }

    @Override
    public CompletableFuture<HttpResponse> execAsync(String method) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        try {
            execDo(method, future);
        } catch (Exception e) {
            future.completeExceptionally(new HttpException(method + " " + _url + ", request failed", e));
        }

        return future;
    }

    /// //////////////////

    protected abstract HttpResponse execDo(String method, CompletableFuture<HttpResponse> future) throws IOException;


    /// //////////////////

    protected String getRequestCookieString(MultiMap<String> cookies) {
        StringBuilder sb = new StringBuilder();

        for (KeyValues<String> kv : cookies) {
            for (String val : kv.getValues()) {
                sb.append(kv.getKey()).append('=').append(val).append("; ");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    protected MultiMap<String> tryInitParams() {
        if (_params == null) {
            _params = new MultiMap<>();
        }
        return _params;
    }

    protected MultiMap<HttpUploadFile> tryInitFiles() {
        if (_files == null) {
            _files = new MultiMap<>();
        }
        return _files;
    }

    protected MultiMap<String> tryInitCookies() {
        if (_cookies == null) {
            _cookies = new MultiMap<>();
        }
        return _cookies;
    }

    protected MultiMap<String> tryInitHeaders() {
        if (_headers == null) {
            _headers = new MultiMap<>();
        }
        return _headers;
    }

    protected static boolean isRedirected(int responseCode) {
        return responseCode == 301
                || responseCode == 302
                || responseCode == 303
                || responseCode == 307 //307和308是RFC 7538中http 1.1定义的规范
                || responseCode == 308;

    }

    /**
     * 获取转发的新地址
     *
     * @param refererUrl 参考URL
     * @param location   定位地址
     */
    public static String getLocationUrl(final String refererUrl, String location) {
        final String redirectUrl;
        if (location.contains("://") == false) {
            URI refererUri = URI.create(refererUrl);

            if (location.startsWith("/") == false) {
                // 可能是相对路径
                String refererPath = refererUri.getPath();
                if (refererPath.endsWith("/") == false) {
                    //定位到目录
                    int tmp = refererPath.lastIndexOf('/');
                    refererPath = refererPath.substring(0, tmp + 1);
                }

                location = refererPath + location;
            }

            redirectUrl = refererUri.getScheme() + "://" + refererUri.getAuthority() + location;
        } else {
            redirectUrl = location;
        }

        return redirectUrl;
    }
}