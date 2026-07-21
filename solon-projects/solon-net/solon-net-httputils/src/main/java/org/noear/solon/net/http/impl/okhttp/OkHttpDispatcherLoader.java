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

import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ThreadsUtil;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.net.http.HttpSslSupplier;
import org.noear.solon.net.http.impl.HttpSslSupplierDefault;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 共享客户端持有者（懒加载）
 * <p>
 * 共享 {@link Dispatcher} 与 {@link ConnectionPool}，通过 {@link OkHttpClient#newBuilder()}
 * 派生请求级差异配置，以实现高并发下的连接复用。
 *
 * @author noear
 * @since 3.9
 */
public class OkHttpDispatcherLoader {
    private static final OkHttpDispatcherLoader INSTANCE = new OkHttpDispatcherLoader();

    private volatile Dispatcher dispatcher;
    private volatile ConnectionPool connectionPool;
    private volatile OkHttpClient baseClient;

    public static OkHttpDispatcherLoader getInstance() {
        return INSTANCE;
    }

    /**
     * 获取共享调度器（线程池）
     */
    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            Utils.locker().lock();
            try {
                if (dispatcher == null) {
                    if (Solon.appIf(app -> app.cfg().isEnabledVirtualThreads())) {
                        dispatcher = new Dispatcher(ThreadsUtil.newVirtualThreadPerTaskExecutor("okhttp-"));
                    } else {
                        dispatcher = new Dispatcher();
                    }
                    dispatcher.setMaxRequests(HttpConfiguration.getMaxRequests());
                    dispatcher.setMaxRequestsPerHost(HttpConfiguration.getMaxRequestsPerHost());
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return dispatcher;
    }

    /**
     * 获取共享连接池
     */
    public ConnectionPool getConnectionPool() {
        if (connectionPool == null) {
            Utils.locker().lock();
            try {
                if (connectionPool == null) {
                    connectionPool = new ConnectionPool(
                            HttpConfiguration.getMaxIdleConnections(),
                            HttpConfiguration.getKeepAliveMinutes(),
                            TimeUnit.MINUTES);
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return connectionPool;
    }

    /**
     * 获取共享基础客户端（含默认超时、SSL、连接池、调度器）
     * <p>请求级差异请使用 {@code getBaseClient().newBuilder()} 派生，以保持连接复用</p>
     */
    public OkHttpClient getBaseClient() {
        if (baseClient == null) {
            Utils.locker().lock();
            try {
                if (baseClient == null) {
                    HttpSslSupplier ssl = HttpSslSupplierDefault.getInstance();

                    baseClient = new OkHttpClient.Builder()
                            .dispatcher(getDispatcher())
                            .connectionPool(getConnectionPool())
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .sslSocketFactory(ssl.getSocketFactory(), ssl.getX509TrustManager())
                            .hostnameVerifier(ssl.getHostnameVerifier())
                            .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                            .followRedirects(true)
                            .followSslRedirects(true)
                            .build();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return baseClient;
    }
}
