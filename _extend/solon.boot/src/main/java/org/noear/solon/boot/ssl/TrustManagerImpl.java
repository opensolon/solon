package org.noear.solon.boot.ssl;

/**
 * @author noear
 * @since 1.6
 */

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class TrustManagerImpl implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {};
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    }
}