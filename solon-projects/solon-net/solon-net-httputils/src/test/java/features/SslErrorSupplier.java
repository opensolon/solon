package features;

import org.noear.solon.net.http.HttpSslSupplier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author noear 2025/8/9 created
 *
 */
public class SslErrorSupplier implements HttpSslSupplier {
    @Override
    public SSLContext getSslContext() {
        return null;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return null;
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return null;
    }
}
