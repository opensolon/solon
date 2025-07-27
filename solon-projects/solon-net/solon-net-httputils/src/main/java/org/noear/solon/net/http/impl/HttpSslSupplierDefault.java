package org.noear.solon.net.http.impl;

import org.noear.solon.Utils;
import org.noear.solon.net.http.HttpSslSupplier;

import javax.net.ssl.*;
import java.security.SecureRandom;
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

    private SSLContext sslContext;
    private HostnameVerifier hostnameVerifier;
    private X509TrustManager x509TrustManager;

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