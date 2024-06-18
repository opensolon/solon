package org.noear.solon.net.http.impl;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Http 请求拦截器实现
 *
 * @author noear
 * @since 2.8
 */
public class HttpInterceptorImpl implements Interceptor {
    public static final HttpInterceptorImpl instance = new HttpInterceptorImpl();

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpTimeout timeout = chain.request().tag(HttpTimeout.class);

        if (timeout != null) {
            if (timeout.connectTimeout > 0) {
                chain = chain.withConnectTimeout(timeout.connectTimeout, TimeUnit.SECONDS);
            }

            if (timeout.writeTimeout > 0) {
                chain = chain.withWriteTimeout(timeout.writeTimeout, TimeUnit.SECONDS);
            }

            if (timeout.readTimeout > 0) {
                chain = chain.withReadTimeout(timeout.readTimeout, TimeUnit.SECONDS);
            }
        }

        return chain.proceed(chain.request());
    }
}
