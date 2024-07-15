/*
 * Copyright 2017-2024 noear.org and authors
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
