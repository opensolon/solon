package org.noear.solon.test;

import okhttp3.Response;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.test.http.TestContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author noear 2023/4/10 created
 */
public class HttpUtilsOfMock implements HttpUtils {
    private String _url;
    private boolean _enablePrintln = false;

    TestContext context = new TestContext();
    public HttpUtilsOfMock(String url){
        _url = url;
        //context.url(url);
    }
    @Override
    public HttpUtils enablePrintln(boolean enable) {
         _enablePrintln = enable;
         return this;
    }

    @Override
    public HttpUtils timeout(int timeoutSeconds) {
        return this;
    }

    @Override
    public HttpUtils timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds) {
        return this;
    }

    @Override
    public HttpUtils multipart(boolean multipart) {
        return this;
    }

    @Override
    public HttpUtils userAgent(String ua) {
        context.headerMap().put("User-Agent", ua);
        return this;
    }

    @Override
    public HttpUtils charset(String charset) {
        //context.charset();
        return this;
    }

    @Override
    public HttpUtils headers(Map<String, String> headers) {
        context.headerMap().putAll(headers);
        return this;
    }

    @Override
    public HttpUtils header(String name, String value) {
        context.headerMap().put(name, value);
        return this;
    }

    @Override
    public HttpUtils headerAdd(String name, String value) {
        context.headerMap().put(name, value);
        return this;
    }

    @Override
    public HttpUtils data(Map data) {
        context.paramMap().putAll(data);
        return this;
    }

    @Override
    public HttpUtils data(String key, String value) {
        context.paramMap().put(key,value);
        return this;
    }

    @Override
    public HttpUtils data(String key, String filename, InputStream inputStream, String contentType) {
        int idx = filename.lastIndexOf('.');
        int size = 0;
        String extension = filename.substring(idx);
        try {
            size = inputStream.available();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        context.filesAdd(key, new UploadedFile(contentType, size, inputStream, filename, extension));
        return this;
    }

    @Override
    public HttpUtils bodyTxt(String txt) {
         return bodyTxt(txt, null);
    }

    @Override
    public HttpUtils bodyTxt(String txt, String contentType) {
        return bodyRaw(txt.getBytes(StandardCharsets.UTF_8), contentType);
    }

    @Override
    public HttpUtils bodyJson(String txt) {
        bodyTxt(txt, "application/json");
        return this;
    }

    @Override
    public HttpUtils bodyRaw(byte[] bytes) {
        return bodyRaw(new ByteArrayInputStream(bytes));
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
        if(contentType !=null) {
            context.headerMap().put("Content-Type", contentType);
        }

        context.bodyAsStream(raw);
        return this;
    }

    @Override
    public HttpUtils cookies(Map cookies) {
        StringBuilder buf = new StringBuilder();

        cookies.forEach((key, val) -> {
            if (buf.length() > 0) {
                buf.append("; ");
            }
            buf.append(key).append("=").append(val);
        });

        context.headerMap().put("Cookie", buf.toString());
        return this;
    }

    @Override
    public Response exec(String mothod) throws IOException {
        //context.method(mothod);
        Solon.app().tryHandle(context);


        return null;
    }

    @Override
    public String execAsBody(String mothod) throws IOException {
        return null;
    }

    @Override
    public int execAsCode(String mothod) throws IOException {
        return 0;
    }

    @Override
    public String get() throws IOException {
        return null;
    }

    @Override
    public String post() throws IOException {
        return null;
    }

    @Override
    public void postAsync() throws IOException {

    }

    @Override
    public void postAsync(HttpCallback<Boolean, Response, Exception> callback) throws IOException {

    }

    @Override
    public void headAsync(HttpCallback<Boolean, Response, Exception> callback) throws IOException {

    }

    @Override
    public String put() throws IOException {
        return null;
    }

    @Override
    public String patch() throws IOException {
        return null;
    }

    @Override
    public String delete() throws IOException {
        return null;
    }

    @Override
    public String options() throws IOException {
        return null;
    }

    @Override
    public int head() throws IOException {
        return 0;
    }
}
