package org.noear.nami.channel;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.nami.NamiException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class OkHttpUtils {
    private final static Dispatcher dispatcher() {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(3000);
        temp.setMaxRequestsPerHost(600);
        return temp;
    }

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60*5, TimeUnit.SECONDS)
            .writeTimeout(60*5, TimeUnit.SECONDS)
            .readTimeout(60*5, TimeUnit.SECONDS)
            .dispatcher(dispatcher())
            .build();

    public static OkHttpUtils http(String url){
        return new OkHttpUtils(url);
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new NamiException(ex);
        }
    }

    private String _url;
    private Charset _charset;
    private Map<String,String> _cookies;
    private RequestBody _body;
    private Map<String,String> _form;
    private MultipartBody.Builder _part_builer;

    private Request.Builder _builder;
    public OkHttpUtils(String url){
        _url = url;
        _builder = new Request.Builder().url(url);
    }

    //@XNote("设置UA")
    public OkHttpUtils userAgent(String ua){
        _builder.header("User-Agent", ua);
        return this;
    }

    //@XNote("设置charset")
    public OkHttpUtils charset(String charset){
        _charset = Charset.forName(charset);
        return this;
    }

    //@XNote("设置请求头")
    public OkHttpUtils headers(Map<String,String> headers){
        if (headers != null) {
            headers.forEach((k, v) -> {
                _builder.header(k, v);
            });
        }

        return this;
    }

    //@XNote("设置请求头")
    public OkHttpUtils header(String name, String value) {
        if(name!=null) {
            _builder.header(name, value);
        }
        return this;
    }

    //@XNote("设置数据提交")
    public OkHttpUtils data(Map<String,Object> data) {
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

    public OkHttpUtils data(String key, String value){
        tryInitForm();
        _form.put(key,value);
        return this;
    }

    //@XNote("设置文件提交")
    public OkHttpUtils data(String key, String filename, InputStream inputStream, String contentType) {
        tryInitPartBuilder(MultipartBody.FORM);

        _part_builer.addFormDataPart(key,
                filename,
                new StreamBody(contentType,inputStream) );

        return this;
    }

    //@XNote("设置BODY提交")
    public OkHttpUtils bodyTxt(String txt, String contentType){
        if(contentType == null) {
            _body = FormBody.create(null, txt);
        }else{
            _body = FormBody.create(MediaType.parse(contentType), txt);
        }

        return this;
    }
    //@XNote("设置BODY提交")
    public OkHttpUtils bodyRaw(InputStream raw, String contentType) {
        _body = new StreamBody(contentType, raw);

        return this;
    }


    //@XNote("设置请求cookies")
    public OkHttpUtils cookies(Map<String,Object> cookies){
        if (cookies != null) {
            tryInitCookies();

            cookies.forEach((k, v) -> {
                _cookies.put(k, v.toString());
            });
        }

        return this;
    }

    //@XNote("执行请求，返回响应对象")
    public Response exec(String mothod) throws Exception {
        if (_part_builer != null) {
            if (_form != null) {
                _form.forEach((k, v) -> {
                    _part_builer.addFormDataPart(k, v);
                });
            }

            _body = _part_builer.build();
        } else {
            if (_form != null) {
                FormBody.Builder fb = new FormBody.Builder(_charset);

                _form.forEach((k, v) -> {
                    fb.add(k, v);
                });

                _body = fb.build();
            }
        }

        if (_cookies != null) {
            _builder.header("Cookie", getRequestCookieString(_cookies));
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
            default: throw new RuntimeException("This method is not supported");
        }

        Call call = httpClient.newCall(_builder.build());
        return call.execute();
    }

    //@XNote("执行请求，返回字符串")
    public String exec2(String mothod) throws Exception {
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
    public String get() throws Exception{
        return exec2("GET");
    }

    //@XNote("发起POST请求，返回字符串（RESTAPI.create 在服务端新建一项资源）")
    public String post() throws Exception{
        return exec2("POST");
    }

    //@XNote("发起PUT请求，返回字符串（RESTAPI.update 客户端提供改变后的完整资源）")
    public String put() throws Exception {
        return exec2("PUT");
    }

    //@XNote("发起PATCH请求，返回字符串（RESTAPI.update 客户端提供改变的属性）")
    public String patch() throws Exception {
        return exec2("PATCH");
    }

    //@XNote("发起DELETE请求，返回字符串（RESTAPI.delete 从服务端删除资源）")
    public String delete() throws Exception {
        return exec2("DELETE");
    }

    private static String getRequestCookieString(Map<String,String> cookies) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for(Map.Entry<String,String> kv : cookies.entrySet()){
            sb.append(kv.getKey()).append('=').append(kv.getValue());
            if (!first) {
                sb.append("; ");
            } else {
                first = false;
            }
        }

        return sb.toString();
    }

    private void tryInitPartBuilder(MediaType type){
        if(_part_builer == null){
            _part_builer = new MultipartBody.Builder().setType(type);
        }
    }

    private void tryInitForm(){
        if(_form ==null){
            _form = new HashMap<>();
        }
    }

    private void tryInitCookies(){
        if(_cookies==null){
            _cookies = new HashMap<>();
        }
    }

    public static class StreamBody extends RequestBody{
        private  MediaType _contentType = null;
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
