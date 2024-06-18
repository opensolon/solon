package org.noear.solon.net.http;

/**
 * Http 回调
 *
 * @author noear
 * @since 2.8
 * */
public interface HttpCallback {
    void callback(Boolean isSuccessful, HttpResponse resp, Exception error) throws Exception;
}
