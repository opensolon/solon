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

import com.moczul.ok2curl.CurlInterceptor;
import okhttp3.OkHttpClient;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.net.http.HttpSslSupplier;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.HttpUtilsFactory;
import org.noear.solon.net.http.impl.HttpSslSupplierDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Http 工具工厂 OkHttp 实现
 *
 * @author noear
 * @since 3.0
 */
public class OkHttpUtilsFactory implements HttpUtilsFactory {
    static final Logger log = LoggerFactory.getLogger(OkHttpUtilsFactory.class);

    private static OkHttpDispatcher dispatcher = new OkHttpDispatcher();

    private static OkHttpClient createHttpClient(Proxy proxy, HttpSslSupplier sslProvider) {
        if (sslProvider == null) {
            sslProvider = HttpSslSupplierDefault.getInstance();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .dispatcher(dispatcher.getDispatcher())
                .addInterceptor(OkHttpInterceptor.instance)
                .sslSocketFactory(sslProvider.getSslContext().getSocketFactory(), sslProvider.getX509TrustManager())
                .hostnameVerifier(sslProvider.getHostnameVerifier());


        if (log.isDebugEnabled() && ClassUtil.hasClass(() -> CurlInterceptor.class)) {
            builder.addInterceptor(new CurlInterceptor(msg -> {
                log.debug(msg);
            }));
        }


        if (proxy != null) {
            builder.proxy(proxy);
        }

        return builder.build();
    }

    /// ////////

    private static final OkHttpUtilsFactory instance = new OkHttpUtilsFactory();

    public static OkHttpUtilsFactory getInstance() {
        return instance;
    }

    /// ////////
    private OkHttpClient defaultClient = createHttpClient(null, null);

    protected OkHttpClient getClient(Proxy proxy, HttpSslSupplier sslProvider) {
        if (proxy == null && sslProvider == null) {
            return defaultClient;
        } else {
            return createHttpClient(proxy, sslProvider);
        }
    }

    /// ////////

    @Override
    public HttpUtils http(String url) {
        return new OkHttpUtils(this, url);
    }
}