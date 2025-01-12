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

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.solon.Utils;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.net.http.*;
import org.noear.solon.net.http.impl.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Http 工具 OkHttp 实现
 *
 * @author noear
 * @since 1.5
 * */
public class OkHttpUtilsImpl extends AbstractHttpUtils implements HttpUtils {
    private final static Supplier<Dispatcher> httpClientDispatcher = () -> {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(20000);
        temp.setMaxRequestsPerHost(10000);
        return temp;
    };

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .dispatcher(httpClientDispatcher.get())
            .addInterceptor(OkHttpInterceptorImpl.instance)
            .sslSocketFactory(HttpSsl.getSSLSocketFactory(), HttpSsl.getX509TrustManager())
            .hostnameVerifier(HttpSsl.defaultHostnameVerifier)
            .build();

    private OkHttpClient _client;

    public OkHttpUtilsImpl(String url) {
        this(url, null);
    }

    public OkHttpUtilsImpl(String url, OkHttpClient client) {
        super(url);

        if (client == null) {
            _client = httpClient;
        } else {
            _client = client;
        }
    }


    @Override
    protected HttpResponse execDo(String _method, CompletableFuture<HttpResponse> future) throws IOException {
        String method = _method.toUpperCase();
        String newUrl = urlRebuild(method, _url, _charset);

        Request.Builder _builder = new Request.Builder().url(newUrl);

        if (_timeout != null) {
            _builder.tag(HttpTimeout.class, _timeout);
        }

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
            _body = new StreamBody(_bodyRaw);
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

        final OkHttpUtilsImpl self = this;

        if (future == null) {
            Call call = _client.newCall(_builder.build());
            return new OkHttpResponseImpl(this, call.execute());
        } else {
            _client.newCall(_builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(e);
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    future.complete(new OkHttpResponseImpl(self, response));
                    //call.cancel();
                }
            });

            return null;
        }
    }

    protected String urlRebuild(String method, String url, Charset charset) throws UnsupportedEncodingException {
        if (_params != null && "GET".equals(method)) {
            StringBuilder newUrl = new StringBuilder(url);

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
            Source source = null;

            try {
                source = Okio.source(_httpStream.getContent());
                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }
}