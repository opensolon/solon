package org.noear.nami.channel.http.okhttp;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class SSLClient {
    public static final DefaultHostnameVerifier defaultHostnameVerifier=new DefaultHostnameVerifier();
    public static final DefaultX509TrustManager defaultTrustManager=new DefaultX509TrustManager();
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

    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{defaultTrustManager}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static X509TrustManager getX509TrustManager() {
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
}
