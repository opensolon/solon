package org.noear.solon.cloud.utils.http;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Http 请求拦截器
 *
 * @author noear
 * @since 1.7
 */
public class HttpInterceptor implements Interceptor {
    public static final HttpInterceptor instance = new HttpInterceptor();

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpTimeout timeout = chain.request().tag(HttpTimeout.class);

        if (timeout != null) {
            if (timeout.connectTimeout > 0) {
                chain.withConnectTimeout(timeout.connectTimeout, TimeUnit.SECONDS);
            }

            if (timeout.writeTimeout > 0) {
                chain.withWriteTimeout(timeout.writeTimeout, TimeUnit.SECONDS);
            }

            if (timeout.readTimeout > 0) {
                chain.withReadTimeout(timeout.readTimeout, TimeUnit.SECONDS);
            }
        }

        return chain.proceed(chain.request());
    }
}
