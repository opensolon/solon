package org.noear.solon.test.http.impl;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.solon.test.HttpCallback;
import org.noear.solon.test.HttpUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class HttpUtilsImpl implements HttpUtils {
    private final static Supplier<Dispatcher> okhttp_dispatcher = () -> {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(3000);
        temp.setMaxRequestsPerHost(600);
        return temp;
    };

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60 * 5, TimeUnit.SECONDS)
            .dispatcher(okhttp_dispatcher.get())
            .addInterceptor(HttpInterceptor.instance)
            .build();


    private Charset _charset;
    private Map<String, String> _cookies;
    private RequestBody _body;
    private List<KeyValue> _form;
    private boolean _multipart = false;

    private MultipartBody.Builder _part_builer;
    private Request.Builder _builder;

    private HttpCallback<Boolean, Response, Exception> _callback;
    private boolean _callAsync;
    private String _url;
    private boolean _enablePrintln = false;

    public HttpUtilsImpl(String url) {
        _builder = new Request.Builder().url(url);
        _url = url;
    }

    @Override
    public HttpUtils enablePrintln(boolean enable) {
        _enablePrintln = enable;
        return this;
    }

    /**
     * 超时设置
     */
    @Override
    public HttpUtils timeout(int timeoutSeconds) {
        if (timeoutSeconds > 0) {
            _builder.tag(HttpTimeout.class, new HttpTimeout(timeoutSeconds));
        }

        return this;
    }

    /**
     * 超时设置
     */
    @Override
    public HttpUtils timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds) {
        if (connectTimeoutSeconds > 0) {
            _builder.tag(HttpTimeout.class, new HttpTimeout(connectTimeoutSeconds, writeTimeoutSeconds, readTimeoutSeconds));
        }

        return this;
    }

    //@XNote("设置multipart")
    @Override
    public HttpUtils multipart(boolean multipart) {
        _multipart = multipart;
        return this;
    }

    //@XNote("设置UA")
    @Override
    public HttpUtils userAgent(String ua) {
        _builder.header("User-Agent", ua);
        return this;
    }

    //@XNote("设置charset")
    @Override
    public HttpUtils charset(String charset) {
        _charset = Charset.forName(charset);
        return this;
    }

    //@XNote("设置请求头")
    @Override
    public HttpUtils headers(Map<String, String> headers) {
        if (headers != null) {
            headers.forEach((k, v) -> {
                _builder.header(k, v);
            });
        }

        return this;
    }

    @Override
    public HttpUtils header(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        _builder.header(name, value);
        return this;
    }

    @Override
    public HttpUtils headerAdd(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        _builder.addHeader(name, value);
        return this;
    }

    @Override
    public HttpUtils data(Map data) {
        if (data != null) {
            tryInitForm();

            data.forEach((k, v) -> {
                if (k != null && v != null) {
                    _form.add(new KeyValue(k.toString(), v.toString()));
                }
            });
        }

        return this;
    }

    @Override
    public HttpUtils data(String key, String value) {
        if (key == null || value == null) {
            return this;
        }

        tryInitForm();
        _form.add(new KeyValue(key, value));
        return this;
    }


    @Override
    public HttpUtils data(String key, String filename, InputStream inputStream, String contentType) {
        if (key == null || inputStream == null) {
            return this;
        }

        multipart(true);
        tryInitPartBuilder(MultipartBody.FORM);

        _part_builer.addFormDataPart(key,
                filename,
                new StreamBody(contentType, inputStream));

        return this;
    }

    @Override
    public HttpUtils bodyTxt(String txt) {
        return bodyTxt(txt, "text/plain");
    }

    @Override
    public HttpUtils bodyTxt(String txt, String contentType) {
        if (txt == null) {
            return this;
        }

        if (contentType == null) {
            _body = FormBody.create(null, txt);
        } else {
            _body = FormBody.create(MediaType.parse(contentType), txt);
        }

        return this;
    }

    @Override
    public HttpUtils bodyJson(String txt) {
        return bodyTxt(txt, "application/json");
    }

    @Override
    public HttpUtils bodyRaw(byte[] bytes) {
        return bodyRaw(bytes, null);
    }

    @Override
    public HttpUtils bodyRaw(byte[] bytes, String contentType) {
        return bodyRaw(new ByteArrayInputStream(bytes), contentType);
    }

    @Override
    public HttpUtils bodyRaw(InputStream raw) {
        return bodyRaw(raw, null);
    }

    @Override
    public HttpUtils bodyRaw(InputStream raw, String contentType) {
        if (raw == null) {
            return this;
        }

        _body = new StreamBody(contentType, raw);

        return this;
    }


    @Override
    public HttpUtils cookies(Map cookies) {
        if (cookies != null) {
            tryInitCookies();

            cookies.forEach((k, v) -> {
                _cookies.put(k.toString(), v.toString());
            });
        }

        return this;
    }


    private void execCallback(Response resp, Exception err) {
        try {
            if (_callback == null) {
                return;
            }

            if (resp != null) {
                _callback.callback(resp.isSuccessful(), resp, err);
            } else {
                _callback.callback(false, null, err);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }

    @Override
    public Response exec(String mothod) throws IOException {
        if (_multipart) {
            tryInitPartBuilder(MultipartBody.FORM);

            if (_form != null) {
                _form.forEach((kv) -> {
                    _part_builer.addFormDataPart(kv.key, kv.value);
                });
            }

            try {
                _body = _part_builer.build();
            } catch (IllegalStateException ex) {
                //这里不要取消（内容为空时，会出错）
            }
        } else {
            if (_form != null) {
                FormBody.Builder fb = new FormBody.Builder(_charset);

                _form.forEach((kv) -> {
                    fb.add(kv.key, kv.value);
                });
                _body = fb.build();
            }
        }

        if (_cookies != null) {
            _builder.header("Cookie", getRequestCookieString(_cookies));
        }

        switch (mothod.toUpperCase()) {
            case "GET":
                _builder.method("GET", null);
                break;
            case "POST":
                _builder.method("POST", _body);
                break;
            case "PUT":
                _builder.method("PUT", _body);
                break;
            case "DELETE":
                _builder.method("DELETE", _body); //有些server只支持queryString参数
                break;
            case "PATCH":
                _builder.method("PATCH", _body);
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
                throw new IllegalStateException("This method is not supported");
        }

        if (_callAsync) {
            httpClient.newCall(_builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    execCallback(null, e);
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    execCallback(response, null);
                    call.cancel();
                }
            });

            return null;
        } else {
            Call call = httpClient.newCall(_builder.build());
            return call.execute();
        }
    }

    @Override
    public String execAsBody(String mothod) throws IOException {
        String rst = exec(mothod).body().string();
        if (_enablePrintln) {
            System.out.println(_url + ":: " + rst);
        }

        return rst;
    }

    @Override
    public int execAsCode(String mothod) throws IOException {
        int code = exec(mothod).code();
        if (_enablePrintln) {
            System.out.println(_url + "::code:: " + code);
        }

        return code;
    }

    @Override
    public String get() throws IOException {
        return execAsBody("GET");
    }

    @Override
    public String post() throws IOException {
        return execAsBody("POST");
    }

    @Override
    public void postAsync() throws IOException {
        postAsync(null);
    }

    @Override
    public void postAsync(HttpCallback<Boolean, Response, Exception> callback) throws IOException {
        _callback = callback;
        _callAsync = true;
        exec("POST");
    }

    @Override
    public void headAsync(HttpCallback<Boolean, Response, Exception> callback) throws IOException {
        _callback = callback;
        _callAsync = true;
        exec("HEAD");
    }

    @Override
    public String put() throws IOException {
        return execAsBody("PUT");
    }

    @Override
    public String patch() throws IOException {
        return execAsBody("PATCH");
    }

    @Override
    public String delete() throws IOException {
        return execAsBody("DELETE");
    }

    @Override
    public String options() throws IOException {
        return execAsBody("OPTIONS");
    }

    @Override
    public int head() throws IOException {
        return execAsCode("HEAD");
    }

    private static String getRequestCookieString(Map<String, String> cookies) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> kv : cookies.entrySet()) {
            sb.append(kv.getKey()).append('=').append(kv.getValue());
            if (!first) {
                sb.append("; ");
            } else {
                first = false;
            }
        }

        return sb.toString();
    }

    private void tryInitPartBuilder(MediaType type) {
        if (_part_builer == null) {
            _part_builer = new MultipartBody.Builder().setType(type);
        }
    }

    private void tryInitForm() {
        if (_form == null) {
            _form = new ArrayList<>();
        }
    }

    private void tryInitCookies() {
        if (_cookies == null) {
            _cookies = new HashMap<>();
        }
    }


    static class StreamBody extends RequestBody {
        private MediaType _contentType = null;
        private InputStream _inputStream = null;

        public StreamBody(String contentType, InputStream inputStream) {
            if (contentType != null) {
                _contentType = MediaType.parse(contentType);
            }

            _inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return _contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return _inputStream.available();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;

            try {
                source = Okio.source(_inputStream);
                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }

    static class KeyValue {
        String key;
        String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
