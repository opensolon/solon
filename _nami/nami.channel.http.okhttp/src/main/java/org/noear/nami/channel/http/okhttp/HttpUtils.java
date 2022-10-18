package org.noear.nami.channel.http.okhttp;

import okhttp3.*;
import org.noear.nami.NamiException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class HttpUtils {
    private final static Supplier<Dispatcher> httpClientDispatcher = () -> {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(20000);
        temp.setMaxRequestsPerHost(10000);
        return temp;
    };

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .dispatcher(httpClientDispatcher.get())
            .addInterceptor(HttpInterceptor.instance)
            .sslSocketFactory(SSLClient.getSSLSocketFactory(), SSLClient.getX509TrustManager())
            .hostnameVerifier(SSLClient.defaultHostnameVerifier)
            .build();

    public static HttpUtils http(String url){
        return new HttpUtils(url);
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new NamiException(ex);
        }
    }

    private Charset _charset;
    private RequestBody _body;
    private Map<String,String> _form;

    private Request.Builder _builder;
    public HttpUtils(String url){
        _builder = new Request.Builder().url(url);
    }


    //@XNote("设置charset")
    public HttpUtils charset(String charset){
        _charset = Charset.forName(charset);
        return this;
    }

    //@XNote("设置请求头")
    public HttpUtils headers(Map<String,String> headers) {
        if (headers != null) {
            headers.forEach((k, v) -> {
                if (v != null) {
                    _builder.header(k, v);
                }
            });
        }

        return this;
    }


    //@XNote("设置数据提交")
    public HttpUtils data(Map<String,Object> data) {
        if (data != null) {
            tryInitForm();

            data.forEach((k, v) -> {
                if (v != null) {
                    _form.put(k, v.toString());
                }
            });
        }

        return this;
    }

    public HttpUtils data(String key, String value){
        tryInitForm();
        _form.put(key,value);
        return this;
    }

    //@XNote("设置BODY提交")
    public HttpUtils bodyRaw(byte[] bytes, String contentType) {
        _body = FormBody.create(MediaType.parse(contentType), bytes);

        return this;
    }

    public HttpUtils timeout(int timeoutSeconds) {
        if (timeoutSeconds > 0) {
            _builder.tag(TimeoutProps.class, new TimeoutProps(timeoutSeconds));
        }

        return this;
    }


    //@XNote("执行请求，返回响应对象")
    public Response exec(String mothod) throws Exception {
        if (_form != null) {
            FormBody.Builder fb = new FormBody.Builder(_charset);

            _form.forEach((k, v) -> {
                fb.add(k, v);
            });

            _body = fb.build();
        }

        switch (mothod.toUpperCase()){
            case "GET":_builder.method("GET",null);break;
            case "POST":_builder.method("POST",_body);break;
            case "PUT":_builder.method("PUT", _body);break;
            case "DELETE":_builder.method("DELETE",_body);break;
            case "PATCH":_builder.method("PATCH",_body);break;
            case "HEAD":_builder.method("HEAD",null);break;
            case "OPTIONS":_builder.method("OPTIONS",null);break;
            case "TRACE":_builder.method("TRACE",null);break;
            default: throw new IllegalStateException("This method is not supported");
        }

        Call call = httpClient.newCall(_builder.build());
        return call.execute();
    }

    private void tryInitForm(){
        if(_form ==null){
            _form = new HashMap<>();
        }
    }
}
