package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.core.util.IoUtil;
import org.noear.solon.net.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * @author noear
 * @since 3.0
 */
public class JdkHttpResponseImpl implements HttpResponse {
    private HttpClient.Response<InputStream> response;

    public JdkHttpResponseImpl(HttpClient.Response<InputStream> response) {
        this.response = response;
    }

    @Override
    public Collection<String> headerNames() {
        return response.getHeader().keySet();
    }

    @Override
    public String header(String name) {
        List<String> values = headers(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    @Override
    public List<String> headers(String name) {
        return response.getHeader().get(name);
    }

    @Override
    public Long contentLength() {
        return response.getHttp().getContentLengthLong();
    }

    @Override
    public String contentType() {
        return response.getHttp().getContentType();
    }

    @Override
    public Charset contentEncoding() {
        return Charset.forName(response.getHttp().getContentEncoding());
    }

    @Override
    public List<String> cookies() {
        return headers("Set-Cookie");
    }

    @Override
    public int code() {
        return response.getStatusCode();
    }

    @Override
    public InputStream body() {
        return response.getBody();
    }

    @Override
    public byte[] bodyAsBytes() throws IOException {
        try {
            return IoUtil.transferToBytes(body());
        } finally {
            body().close();
        }
    }

    @Override
    public String bodyAsString() throws IOException {
        try {
            return IoUtil.transferToString(body());
        } finally {
            body().close();
        }
    }
}
