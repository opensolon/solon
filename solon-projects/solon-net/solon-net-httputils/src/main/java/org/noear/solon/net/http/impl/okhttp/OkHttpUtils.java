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

import com.moczul.ok2curl.CurlInterceptor;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.net.http.*;
import org.noear.solon.net.http.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Http 工具 OkHttp 实现
 *
 * @author noear
 * @since 1.5
 * @since 3.9 共享连接池与调度器，支持高并发连接复用
 */
public class OkHttpUtils extends AbstractHttpUtils implements HttpUtils {
    protected static final Logger log = LoggerFactory.getLogger(OkHttpUtils.class);
    /** 共享调度器与连接池，见 {@link OkHttpDispatcherLoader} */
    protected static final OkHttpDispatcherLoader dispatcherLoader = OkHttpDispatcherLoader.getInstance();

    private int redirectCount;

    public OkHttpUtils(String url) {
        super(url);
    }

    private RequestBody _bodyRaw;

    /**
     * 设置 BODY txt 及内容类型
     */
    @Override
    public HttpUtils body(String txt, String contentType) {
        if (txt != null) {
//            _bodyRaw = RequestBody.create(txt, contentType == null ? null : MediaType.parse(contentType));
            _bodyRaw = RequestBody.create(txt.getBytes(_charset), contentType == null ? null : MediaType.parse(contentType));
        }

        return this;
    }

    @Override
    public HttpUtils bodyOfBean(Object obj) throws HttpException {
        Object tmp;
        try {
            tmp = serializer().serialize(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        if (tmp instanceof String) {
            body((String) tmp, serializer().mimeType());
        } else if (tmp instanceof byte[]) {
            body((byte[]) tmp, serializer().mimeType());
        } else {
            throw new IllegalArgumentException("Invalid serializer type!");
        }

        return this;
    }

    @Override
    public HttpUtils body(byte[] bytes, String contentType) {
        if (bytes != null) {
            _bodyRaw = RequestBody.create(bytes, contentType == null ? null : MediaType.parse(contentType));
        }

        return this;
    }

    @Override
    public HttpUtils body(InputStream raw, String contentType) {
        if (raw != null) {
            _bodyRaw = new StreamBody(new HttpStream(raw, contentType));
        }

        return this;
    }

    @Override
    protected HttpResponse execDo(String _method, CompletableFuture<HttpResponse> future) throws IOException {
        String method = _method.toUpperCase();
        String newUrl = urlRebuild(method, _url, _charset);

        Request.Builder _builder = new Request.Builder().url(newUrl);

        if (_headers != null) {
            _headers.forEach(kv -> {
                for (String val : kv.getValues()) {
                    _builder.addHeader(kv.getKey(), val);
                }
            });
        }
        if (_cookies != null) {
            _builder.header("Cookie", getRequestCookieString(_cookies));
        }

        RequestBody _body = null;

        if (_bodyRaw != null) {
            _body = _bodyRaw;
        } else {
            if (_multipart) {
                MultipartBody.Builder _part_builer = new MultipartBody.Builder().setType(MultipartBody.FORM);

                if (Utils.isEmpty(_files) == false) {
                    for (KeyValues<HttpUploadFile> kv : _files) {
                        for (HttpUploadFile val : kv.getValues()) {
                            _part_builer.addFormDataPart(kv.getKey(), val.fileName, new StreamBody(val.fileStream));
                        }
                    }
                }

                if (Utils.isEmpty(_params) == false) {
                    for (KeyValues<String> kv : _params) {
                        for (String val : kv.getValues()) {
                            _part_builer.addFormDataPart(kv.getKey(), val);
                        }
                    }
                }

                try {
                    _body = _part_builer.build();
                } catch (IllegalStateException ex) {
                    //这里不要取消（内容为空时，会出错）
                }
            } else if (Utils.isEmpty(_params) == false) {
                FormBody.Builder _form_builer = new FormBody.Builder(_charset);

                for (KeyValues<String> kv : _params) {
                    for (String val : kv.getValues()) {
                        _form_builer.add(kv.getKey(), val);
                    }
                }
                _body = _form_builer.build();
            } else {
                //HEAD 可以为空
            }
        }

        if (_body == null) {
            _body = new FormBody.Builder(_charset).build();
        }


        switch (method.toUpperCase()) {
            case "POST":
                _builder.method("POST", _body);
                break;
            case "PUT":
                _builder.method("PUT", _body);
                break;
            case "DELETE":
                _builder.method("DELETE", _body);
                break;
            case "PATCH":
                _builder.method("PATCH", _body);
                break;
            case "GET":
                _builder.method("GET", null);
                break;
            case "HEAD":
                _builder.method("HEAD", null);
                break;
            case "OPTIONS":
                _builder.method("OPTIONS", null);
                break;
            case "TRACE":
                _builder.method("TRACE", null);
                break;
            default:
                throw new IllegalArgumentException("This method is not supported");
        }

        OkHttpClient _client = getClient();

        if (future == null) {
            Call call = _client.newCall(_builder.build());
            return getResponse(call.execute(), method);
        } else {
            _client.newCall(_builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(e);
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    future.complete(getResponse(response, method));
                    //call.cancel();
                }
            });

            return null;
        }
    }

    /**
     * 获取请求级 OkHttpClient（从共享 base newBuilder 派生，保持连接池与调度器复用）
     */
    protected OkHttpClient getClient() {
        if (_sslSupplier == null) {
            _sslSupplier = HttpSslSupplierDefault.getInstance();
        }

        OkHttpClient base = dispatcherLoader.getBaseClient();

        // 快路径：默认超时 + 默认 SSL + 无代理 + 无调试拦截器时直接复用 base
        boolean needDebugInterceptor = log.isDebugEnabled() && ClassUtil.hasClass(() -> CurlInterceptor.class);
        boolean defaultSsl = (_sslSupplier == HttpSslSupplierDefault.getInstance());
        boolean defaultTimeout = isDefaultTimeout(_timeout);

        if (defaultTimeout && defaultSsl && _proxy == null && !needDebugInterceptor) {
            return base;
        }

        // newBuilder() 会共享 connectionPool 与 dispatcher
        OkHttpClient.Builder builder = base.newBuilder();

        if (_timeout != null) {
            builder.connectTimeout(timeoutMillis(_timeout.getConnectTimeout()), TimeUnit.MILLISECONDS)
                    .writeTimeout(timeoutMillis(_timeout.getWriteTimeout()), TimeUnit.MILLISECONDS)
                    .readTimeout(timeoutMillis(_timeout.getReadTimeout()), TimeUnit.MILLISECONDS);
        }

        if (!defaultSsl) {
            builder.sslSocketFactory(_sslSupplier.getSocketFactory(), _sslSupplier.getX509TrustManager())
                    .hostnameVerifier(_sslSupplier.getHostnameVerifier());
        }

        if (needDebugInterceptor) {
            builder.addInterceptor(new CurlInterceptor(msg -> {
                log.debug(msg);
            }));
        }

        if (_proxy != null) {
            builder.proxy(_proxy);
        }

        return builder.build();
    }

    private static long timeoutMillis(Duration d) {
        return d == null ? 0L : Math.max(0L, d.toMillis());
    }

    private static boolean isDefaultTimeout(HttpTimeout timeout) {
        if (timeout == null) {
            return true;
        }
        return timeoutMillis(timeout.getConnectTimeout()) == 10_000L
                && timeoutMillis(timeout.getWriteTimeout()) == 60_000L
                && timeoutMillis(timeout.getReadTimeout()) == 60_000L;
    }

    protected List<ConnectionSpec> getConnectionSpecs() {
        // 已内联到 OkHttpDispatcherLoader，此方法保留以兼容子类覆写
        return Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT);
    }

    protected HttpResponse getResponse(Response response, String method) throws IOException {
        int statusCode = response.code();

        if (isRedirected(statusCode)) {
            if (++redirectCount > HttpConfiguration.getMaxRedirects()) {
                response.close();
                throw new IOException("Too many redirects: " + redirectCount + ", url: " + _url);
            }

            String location = response.header("Location");
            // 重定向响应体不再使用，关闭以归还连接
            response.close();

            if (Utils.isEmpty(location)) {
                //如果没有，则异常
                throw new IOException("Redirect location header unfound, original url: " + _url);
            }

            _url = getLocationUrl(_url, location);

            return execDo(method, null);
        } else {
            return new OkHttpResponse(this, statusCode, response);
        }
    }

    protected String urlRebuild(String method, String url, Charset charset) throws UnsupportedEncodingException {
        if (_params != null && "GET".equals(method)) {
            StringBuilder newUrl = new StringBuilder(url);

            for (KeyValues<String> kv : _params) {
                String key = HttpUtils.urlEncode(kv.getKey(), charset.name());
                for (String val : kv.getValues()) {
                    if (val != null) {
                        if (newUrl.indexOf("?") < 0) {
                            newUrl.append("?");
                        } else {
                            newUrl.append("&");
                        }
                        newUrl.append(key).append("=").append(HttpUtils.urlEncode(val, charset.name()));
                    }
                }
            }

            _params.clear();
            return newUrl.toString();
        } else {
            return url;
        }
    }

    public static class StreamBody extends RequestBody {
        private MediaType _contentType = null;
        private HttpStream _httpStream = null;

        public StreamBody(HttpStream stream) {
            if (stream.getContentType() != null) {
                _contentType = MediaType.parse(stream.getContentType());
            }

            this._httpStream = stream;
        }

        @Override
        public MediaType contentType() {
            return _contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return _httpStream.getContentLength();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            try (Source source = Okio.source(_httpStream.getContent())) {
                sink.writeAll(source);
            }
        }
    }
}