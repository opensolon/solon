/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.net.http.impl.okhttp;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.noear.solon.net.http.HttpTimeout;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Http 请求 OkHttp 拦截器实现
 *
 * @author noear
 * @since 2.8
 */
public class OkHttpInterceptor implements Interceptor {
    public static final OkHttpInterceptor instance = new OkHttpInterceptor();

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
