package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.HttpUtilsFactory;

/**
 * Http 工具工厂 JDK HttpURLConnection 实现
 *
 * @author noear
 * @since 3.0
 */
public class JdkHttpUtilsFactory implements HttpUtilsFactory {
    @Override
    public HttpUtils http(String url) {
        return new JdkHttpUtilsImpl(url);
    }
}
