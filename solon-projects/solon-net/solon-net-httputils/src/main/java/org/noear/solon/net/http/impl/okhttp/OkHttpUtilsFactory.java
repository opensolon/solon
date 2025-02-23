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

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.noear.solon.Utils;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.HttpUtilsFactory;
import org.noear.solon.net.http.impl.HttpSsl;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Http 工具工厂 OkHttp 实现
 *
 * @author noear
 * @since 3.0
 */
public class OkHttpUtilsFactory implements HttpUtilsFactory {
    private final static Supplier<Dispatcher> httpClientDispatcher = () -> {
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(20000);
        temp.setMaxRequestsPerHost(10000);
        return temp;
    };

    private static OkHttpClient createHttpClient(String proxyHost, int proxyPort) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .dispatcher(httpClientDispatcher.get())
                .addInterceptor(OkHttpInterceptor.instance)
                .sslSocketFactory(HttpSsl.getSSLSocketFactory(), HttpSsl.getX509TrustManager())
                .hostnameVerifier(HttpSsl.defaultHostnameVerifier);

        if (Utils.isNotEmpty(proxyHost)) {
            Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            builder.proxy(httpProxy);
        }

        return builder.build();
    }

    /// ////////

    private static final OkHttpUtilsFactory instance = new OkHttpUtilsFactory();

    public static OkHttpUtilsFactory getInstance() {
        return instance;
    }

    private Map<String, OkHttpClient> proxyClients = new ConcurrentHashMap<>();
    private OkHttpClient defaultClient = createHttpClient(null, 0);

    protected OkHttpClient getClient(String proxyHost, int proxyPort) {
        if (proxyHost == null) {
            return defaultClient;
        } else {
            String key = proxyHost + ":" + proxyPort;
            return proxyClients.computeIfAbsent(key, k -> createHttpClient(proxyHost, proxyPort));
        }
    }

    @Override
    public HttpUtils http(String url) {
        return new OkHttpUtils(this, url);
    }
}
