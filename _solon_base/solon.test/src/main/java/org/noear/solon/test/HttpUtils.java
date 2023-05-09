package org.noear.solon.test;

import okhttp3.Response;
import org.noear.solon.Solon;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.test.http.impl.HttpUtilsImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public interface HttpUtils {
    static HttpUtils http(String service, String path) {
        String url = LoadBalance.get(service).getServer() + path;
        return http(url);
    }

    static HttpUtils http(String group, String service, String path) {
        String url = LoadBalance.get(group, service).getServer() + path;
        return http(url);
    }

    static HttpUtils http(String url) {
        return new HttpUtilsImpl(url);
    }

    HttpUtils enablePrintln(boolean enable);

    HttpUtils timeout(int timeoutSeconds);

    HttpUtils timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds);

    HttpUtils multipart(boolean multipart);

    HttpUtils userAgent(String ua);

    HttpUtils charset(String charset);

    HttpUtils headers(Map<String, String> headers);

    HttpUtils header(String name, String value);

    HttpUtils headerAdd(String name, String value);

    HttpUtils data(Map data);

    HttpUtils data(String key, String value);

    HttpUtils data(String key, String filename, InputStream inputStream, String contentType);

    HttpUtils bodyTxt(String txt);

    HttpUtils bodyTxt(String txt, String contentType);

    HttpUtils bodyJson(String txt);

    HttpUtils bodyRaw(byte[] bytes);

    HttpUtils bodyRaw(byte[] bytes, String contentType);

    HttpUtils bodyRaw(InputStream raw);

    HttpUtils bodyRaw(InputStream raw, String contentType);

    HttpUtils cookies(Map cookies);

    Response exec(String mothod) throws IOException;

    String execAsBody(String mothod) throws IOException;

    int execAsCode(String mothod) throws IOException;

    String get() throws IOException;

    String post() throws IOException;

    void postAsync() throws IOException;

    void postAsync(HttpCallback<Boolean, Response, Exception> callback) throws IOException;

    void headAsync(HttpCallback<Boolean, Response, Exception> callback) throws IOException;

    String put() throws IOException;

    String patch() throws IOException;

    String delete() throws IOException;

    String options() throws IOException;

    int head() throws IOException;

    static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, Solon.encoding());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static String toQueryString(Map<?, ?> map) throws UnsupportedEncodingException {
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
}
