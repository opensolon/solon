package demo.httputils;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.net.http.HttpExtension;
import org.noear.solon.net.http.HttpSslSupplier;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.HttpSslSupplierDefault;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 *
 * @author noear 2025/8/8 created
 *
 */
@Component
public class HttpSslExtensionDemo implements HttpExtension, HttpSslSupplier {
    // for HttpExtension
    @Override
    public void onInit(HttpUtils httpUtils, String url) {
        httpUtils.ssl(this);
    }

    //for HttpSslSupplier （按需修改）
    private SSLContext sslContext;
    private HostnameVerifier hostnameVerifier;
    private X509TrustManager x509TrustManager;

    @Override
    public SSLContext getSslContext() {
        if (sslContext == null) {
            Utils.locker().lock();
            try {
                if (sslContext == null) {
                    sslContext = SSLContext.getInstance("TLS");
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
                    hostnameVerifier = new HttpSslSupplierDefault.DefaultHostnameVerifier();
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
                    x509TrustManager = new HttpSslSupplierDefault.DefaultX509TrustManager();
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
