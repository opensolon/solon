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

import org.noear.solon.Utils;
import org.noear.solon.net.http.HttpSslSupplier;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * SSL 客户端
 *
 * @author desire
 * @since noear
 * */
public class HttpSslSupplierDefault implements HttpSslSupplier {
    private static HttpSslSupplier instance = new HttpSslSupplierDefault();

    public static HttpSslSupplier getInstance() {
        return instance;
    }

    private HostnameVerifier hostnameVerifier;
    private X509TrustManager x509TrustManager;

    @Override
    public SSLContext getSslContext() {
        try {
            return SSLContext.getDefault();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public SSLSocketFactory getSocketFactory() {
        return getSslContext().getSocketFactory();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        if (hostnameVerifier == null) {
            Utils.locker().lock();
            try {
                if (hostnameVerifier == null) {
                    hostnameVerifier = new DefaultHostnameVerifier();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return hostnameVerifier;
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        if (x509TrustManager == null) {
            Utils.locker().lock();
            try {
                if (x509TrustManager == null) {
                    x509TrustManager = new DefaultX509TrustManager();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return x509TrustManager;
    }

    public static class DefaultX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}