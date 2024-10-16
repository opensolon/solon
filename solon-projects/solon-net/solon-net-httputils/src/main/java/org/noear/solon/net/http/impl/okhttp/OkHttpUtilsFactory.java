package org.noear.solon.net.http.impl.okhttp;

import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.HttpUtilsFactory;

/**
 * Http 工具工厂 OkHttp 实现
 *
 * @author noear
 * @since 3.0
 */
public class OkHttpUtilsFactory implements HttpUtilsFactory {
    @Override
    public HttpUtils http(String url) {
        return new OkHttpUtilsImpl(url);
    }
}
