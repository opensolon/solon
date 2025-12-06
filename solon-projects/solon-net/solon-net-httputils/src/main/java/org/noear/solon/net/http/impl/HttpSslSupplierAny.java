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
package org.noear.solon.net.http.impl;

import org.noear.solon.net.http.HttpSslSupplier;
import org.noear.solon.net.http.ssl.SslAnyHostnameVerifier;
import org.noear.solon.net.http.ssl.SslAnyTrustManager;
import org.noear.solon.net.http.ssl.SslContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * SSL 客户端
 *
 * @author noear
 * @since 3.7
 * */
public class HttpSslSupplierAny implements HttpSslSupplier {
    private static HttpSslSupplier instance = new HttpSslSupplierAny();

    public static HttpSslSupplier getInstance() {
        return instance;
    }


    private SSLContext anySslContext;

    @Override
    public SSLContext getSslContext() {
        if (anySslContext == null) {
            try {
                anySslContext = SslContextBuilder.of()
                        .trustManagers(getX509TrustManager())
                        .build();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        return anySslContext;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return SslAnyHostnameVerifier.INSTANCE;
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return SslAnyTrustManager.INSTANCE;
    }
}