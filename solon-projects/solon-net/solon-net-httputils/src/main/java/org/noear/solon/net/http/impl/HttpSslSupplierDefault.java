package org.noear.solon.net.http.impl;

import org.noear.solon.Utils;
import org.noear.solon.net.http.HttpSslSupplier;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

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

    private SSLContext sslContext;
    private HostnameVerifier hostnameVerifier;

    @Override
    public SSLContext getSslContext() {
        if (sslContext == null) {
            Utils.locker().lock();
            try {
                if (sslContext == null) {
                    sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, new TrustManager[]{getX509TrustManager()}, new SecureRandom());
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                Utils.locker().unlock();
            }
        }

        return sslContext;
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

    public X509TrustManager getX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trustManager;
    }

    public static class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}