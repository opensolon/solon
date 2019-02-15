package org.noear.solonclient;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class HttpUtil {
    ////////////////////////////
    private static Dispatcher dispatcher() {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(3000);
        temp.setMaxRequestsPerHost(300);
        return temp;
    }

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .dispatcher(dispatcher())
            .build();

    public static String getString(String url) throws IOException {
        return getString(url, null);
    }

    public static String getString(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (headers != null) {
            headers.forEach((k, v) -> builder.header(k, v));
        }

        return execCall(builder);
    }

    public static String postString(String url, Map<String, String> params) throws IOException {
        return postString(url, params, null);
    }

    public static String postString(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        FormBody.Builder form = new FormBody.Builder();
        if (params != null) {
            params.forEach((k, v) -> {
                form.add(k, v);
            });
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(form.build());

        if (headers != null) {
            headers.forEach((k, v) -> builder.header(k, v));
        }

        return execCall(builder);
    }

    public static String postString(String url, String rawParams, Map<String, String> headers) throws IOException {
        RequestBody body = FormBody.create(MediaType.parse("text/plain; charset=utf-8"), rawParams);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        if (headers != null) {
            headers.forEach((k, v) -> builder.header(k, v));
        }

        return execCall(builder);
    }

    private static String execCall(Request.Builder builder) throws IOException {
        Call call = httpClient.newCall(builder.build());
        Response response = call.execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }
        String temp = response.body().string();
        call.cancel();

        return temp;
    }
}
