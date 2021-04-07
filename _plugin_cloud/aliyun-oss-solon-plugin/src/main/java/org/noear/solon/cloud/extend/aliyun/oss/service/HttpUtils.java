package org.noear.solon.cloud.extend.aliyun.oss.service;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 1.3
 */
class HttpUtils {
    private final static Supplier<Dispatcher> okhttp_dispatcher = () -> {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(20000);
        temp.setMaxRequestsPerHost(10000);
        return temp;
    };

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60 * 5, TimeUnit.SECONDS)
            .writeTimeout(60 * 5, TimeUnit.SECONDS)
            .readTimeout(60 * 5, TimeUnit.SECONDS)
            .dispatcher(okhttp_dispatcher.get())
            .build();

    public static HttpUtils http(String url) {
        return new HttpUtils(url);
    }

    private RequestBody _body;
    private Request.Builder _builder;

    public HttpUtils(String url) {
        _builder = new Request.Builder().url(url);
    }

    //@XNote("设置请求头")
    public HttpUtils header(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        _builder.header(name, value);
        return this;
    }

    //@XNote("设置BODY txt及内容类型")
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

    //@XNote("设置BODY raw及内容类型")
    public HttpUtils bodyRaw(InputStream raw, String contentType) {
        if(raw == null){
            return this;
        }

        _body = new StreamBody(contentType, raw);

        return this;
    }


    //@XNote("执行请求，返回响应对象")
    public Response exec(String mothod) throws IOException {
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
            default:
                throw new RuntimeException("This method is not supported");
        }

        Call call = httpClient.newCall(_builder.build());
        return call.execute();
    }

    //@XNote("执行请求，返回字符串")
    public String exec2(String mothod) throws IOException {
        Response tmp = exec(mothod);

        int code = tmp.code();
        String text = tmp.body().string();
        if (code >= 200 && code <= 300) {
            return text;
        } else {
            throw new RuntimeException(code + "错误：" + text);
        }
    }


    //@XNote("发起GET请求，返回字符串（RESTAPI.select 从服务端获取一或多项资源）")
    public String get() throws IOException {
        return exec2("GET");
    }

    //@XNote("发起PUT请求，返回字符串（RESTAPI.update 客户端提供改变后的完整资源）")
    public String put() throws IOException {
        return exec2("PUT");
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
}