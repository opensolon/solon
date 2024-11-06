package org.noear.solon.net.http.impl;

import okhttp3.OkHttpClient;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.HttpUtilsFactory;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsImpl;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsImpl;

/**
 * @author noear
 * @since 3.0
 */
public class HttpUtilsFactoryDefault implements HttpUtilsFactory {
    @Override
    public HttpUtils http(String url) {
        if (ClassUtil.hasClass(() -> OkHttpClient.class)) {
            return new OkHttpUtilsImpl(url);
        } else {
            return new JdkHttpUtilsImpl(url);
        }
    }
}
