package org.noear.solon.net.http.impl;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.solon.Solon;
import org.noear.solon.net.http.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Http 工具实现
 *
 * @author noear
 * @since 1.5
 * */
public class HttpUtilsImpl implements HttpUtils {
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
            .addInterceptor(HttpInterceptorImpl.instance)
            .build();

    private OkHttpClient _client;
    private String _url;
    private Charset _charset;
    private Map<String, String> _cookies;
    private RequestBody _body;
    private List<KeyValue> _form;
    private boolean _multipart = false;

    private MultipartBody.Builder _part_builer;
    private final Request.Builder _builder;

    private HttpCallback _callback;
    private boolean _callAsync;
    private boolean _enablePrintln = false;

    public HttpUtilsImpl(String url) {
        this(url, null);
    }

    public HttpUtilsImpl(String url, OkHttpClient client) {
        if (client == null) {
            _client = httpClient;
        } else {
            _client = client;
        }

        if (url.contains("://") == false) {
            throw new IllegalArgumentException("No url scheme 'http' or 'https' found: " + url);

        }

        _url = url;
        _builder = new Request.Builder().url(url);
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
    public HttpUtilsImpl timeout(int timeoutSeconds) {
        if (timeoutSeconds > 0) {
            _builder.tag(HttpTimeout.class, new HttpTimeout(timeoutSeconds));
        }

        return this;
    }

    /**
     * 超时设置
     */
    @Override
    public HttpUtilsImpl timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds) {
        if (connectTimeoutSeconds > 0) {
            _builder.tag(HttpTimeout.class, new HttpTimeout(connectTimeoutSeconds, writeTimeoutSeconds, readTimeoutSeconds));
        }

        return this;
    }

    /**
     * 设置 multipart 标识
     */
    @Override
    public HttpUtilsImpl multipart(boolean multipart) {
        _multipart = multipart;
        return this;
    }

    /**
     * 设置 UA
     */
    @Override
    public HttpUtilsImpl userAgent(String ua) {
        _builder.header("User-Agent", ua);
        return this;
    }

    /**
     * 设置 charset
     */
    @Override
    public HttpUtilsImpl charset(String charset) {
        _charset = Charset.forName(charset);
        return this;
    }

    /**
     * 设置请求头
     */
    @Override
    public HttpUtilsImpl headers(Map<String, String> headers) {
        if (headers != null) {
            headers.forEach((k, v) -> {
                _builder.header(k, v);
            });
        }

        return this;
    }

    /**
     * 设置请求头
     */
    @Override
    public HttpUtilsImpl header(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        _builder.header(name, value);
        return this;
    }

    /**
     * 添加请求头
     */
    @Override
    public HttpUtilsImpl headerAdd(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        _builder.addHeader(name, value);
        return this;
    }

    /**
     * 设置表单数据
     */
    @Override
    public HttpUtilsImpl data(Map<String, String> data) {
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

    /**
     * 设置表单数据
     */
    @Override
    public HttpUtilsImpl data(String key, String value) {
        if (key == null || value == null) {
            return this;
        }

        tryInitForm();
        _form.add(new KeyValue(key, value));
        return this;
    }


    /**
     * 设置表单文件
     */
    @Override
    public HttpUtilsImpl data(String key, String filename, InputStream inputStream, String contentType) {
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

    /**
     * 设置 BODY txt
     */
    @Override
    public HttpUtilsImpl bodyTxt(String txt) {
        return bodyTxt(txt, null);
    }

    /**
     * 设置 BODY txt 及内容类型
     */
    @Override
    public HttpUtilsImpl bodyTxt(String txt, String contentType) {
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

    /**
     * 设置 BODY josn
     */
    @Override
    public HttpUtilsImpl bodyJson(String txt) {
        return bodyTxt(txt, "application/json");
    }

    /**
     * 设置 BODY raw
     */
    @Override
    public HttpUtilsImpl bodyRaw(byte[] bytes) {
        return bodyRaw(bytes, null);
    }

    /**
     * "设置 BODY raw 及内容类型
     */
    @Override
    public HttpUtilsImpl bodyRaw(byte[] bytes, String contentType) {
        return bodyRaw(new ByteArrayInputStream(bytes), contentType);
    }

    /**
     * 设置 BODY raw stream
     */
    @Override
    public HttpUtilsImpl bodyRaw(InputStream raw) {
        return bodyRaw(raw, null);
    }

    /**
     * 设置 BODY raw stream 及内容类型
     */
    @Override
    public HttpUtilsImpl bodyRaw(InputStream raw, String contentType) {
        if (raw == null) {
            return this;
        }

        _body = new StreamBody(contentType, raw);

        return this;
    }


    /**
     * 设置请求 cookies
     */
    @Override
    public HttpUtilsImpl cookies(Map<String, String> cookies) {
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
                _callback.callback(resp.isSuccessful(), new HttpResponseImpl(resp), err);
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


    private HttpResponse execDo(String mothod) throws IOException {
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
                _builder.method("DELETE", _body);
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
                throw new IllegalArgumentException("This method is not supported");
        }

        if (_callAsync) {
            _client.newCall(_builder.build()).enqueue(new Callback() {
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
            Call call = _client.newCall(_builder.build());
            return new HttpResponseImpl(call.execute());
        }
    }


    /**
     * 执行请求，返回响应对象
     */
    @Override
    public HttpResponse exec(String method) throws IOException {
        try {
            return execDo(method);
        } catch (IOException e) {
            throw new IOException(_url + ", request failed", e);
        }
    }

    /**
     * 执行请求，返回Body字符串
     */
    @Override
    public String execAsBody(String method) throws IOException {
        String text = exec(method).bodyAsString();

        if (_enablePrintln) {
            System.out.println(_url + ":: " + text);
        }

        return text;
    }

    /**
     * 执行请求，返回状态码
     */
    @Override
    public int execAsCode(String method) throws IOException {
        int code = exec(method).code();

        if (_enablePrintln) {
            System.out.println(_url + "::code:: " + code);
        }

        return code;
    }

    /**
     * 发起GET请求，返回字符串（RESTAPI.select 从服务端获取一或多项资源）
     */
    @Override
    public String get() throws IOException {
        return execAsBody("GET");
    }

    /**
     * 发起POST请求，返回字符串（RESTAPI.create 在服务端新建一项资源）
     */
    @Override
    public String post() throws IOException {
        return execAsBody("POST");
    }

    @Override
    public void postAsync(HttpCallback callback) {
        execAsync("POST", callback);
    }

    @Override
    public void getAsync(HttpCallback callback) {
        execAsync("GET", callback);
    }

    @Override
    public void headAsync(HttpCallback callback) {
        execAsync("HEAD", callback);
    }

    @Override
    public void execAsync(String method, HttpCallback callback) {
        _callback = callback;
        _callAsync = true;
        try {
            execDo(method);
        } catch (IOException e) {
            throw new RuntimeException(_url + ", request failed", e);
        }
    }

    /**
     * 发起PUT请求，返回字符串（RESTAPI.update 客户端提供改变后的完整资源）
     */
    @Override
    public String put() throws IOException {
        return execAsBody("PUT");
    }

    /**
     * 发起PATCH请求，返回字符串（RESTAPI.update 客户端提供改变的属性）
     */
    @Override
    public String patch() throws IOException {
        return execAsBody("PATCH");
    }

    /**
     * 发起DELETE请求，返回字符串（RESTAPI.delete 从服务端删除资源）
     */
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

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, Solon.encoding());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String toQueryString(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncode(entry.getKey().toString()),
                    urlEncode(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    public static class StreamBody extends RequestBody {
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

    public static class KeyValue {
        String key;
        String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
