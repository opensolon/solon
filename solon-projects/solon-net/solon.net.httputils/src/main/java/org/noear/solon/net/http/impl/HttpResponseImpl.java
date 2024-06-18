package org.noear.solon.net.http.impl;

import okhttp3.Response;
import org.noear.solon.net.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Http 响应实现
 *
 * @author noear
 * @since 2.8
 */
public class HttpResponseImpl implements HttpResponse {
    private Response response;

    public HttpResponseImpl(Response response) {
        this.response = response;
    }

    @Override
    public String header(String name) {
        return response.header(name);
    }

    @Override
    public List<String> headers(String name) {
        return response.headers(name);
    }

    @Override
    public Long contentLength() {
        return response.body().contentLength();
    }

    @Override
    public String contentType() {
        return response.body().contentType().type();
    }

    @Override
    public List<String> cookies() {
        return response.headers("Set-Cookie");
    }

    @Override
    public int code() {
        return response.code();
    }

    @Override
    public InputStream body() {
        return response.body().byteStream();
    }

    @Override
    public byte[] bodyAsBytes() throws IOException {
        return response.body().bytes();
    }

    @Override
    public String bodyAsString() throws IOException {
        return response.body().string();
    }
}
