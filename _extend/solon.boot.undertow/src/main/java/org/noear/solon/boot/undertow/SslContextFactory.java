package org.noear.solon.boot.undertow;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author noear
 * @since 1.6
 */
public class SslContextFactory {
    public static SSLContext createSslContext() throws KeyManagementException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        String sslKeyStore = System.getProperty(ServerConstants.SSL_KEYSTORE);
        String sslKeyStoreType = System.getProperty(ServerConstants.SSL_KEYSTORE_TYPE);
        String sslKeyStorePassword = System.getProperty(ServerConstants.SSL_KEYSTORE_PASSWORD);

        return createSslContext(sslKeyStore, sslKeyStoreType, sslKeyStorePassword);
    }

    public static SSLContext createSslContext(String sslKeyStore, String sslKeyStoreType, String sslKeyStorePassword) throws KeyManagementException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManager[] keyManagers = getKeyManagers(sslKeyStore, sslKeyStoreType, sslKeyStorePassword);

        sslContext.init(keyManagers, null, null);

        return sslContext;
    }

    protected static KeyManager[] getKeyManagers(String sslKeyStore, String sslKeyStoreType, String sslKeyStorePassword) {
        char[] keyStorePassword = null;
        if (Utils.isNotEmpty(sslKeyStorePassword)) {
            keyStorePassword = sslKeyStorePassword.toCharArray();
        }

        try {
            KeyStore keyStore = getKeyStore(sslKeyStore, sslKeyStoreType, keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(keyStore, keyStorePassword);

            return keyManagerFactory.getKeyManagers();

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected static KeyStore getKeyStore(String sslKeyStore, String sslKeyStoreType, char[] keyStorePassword) throws Exception {
        URL keyStoreUrl = Utils.getResource(sslKeyStore);
        InputStream keyStoreStream;

        if (keyStoreUrl != null) {
            keyStoreStream = keyStoreUrl.openStream();
        } else {
            keyStoreStream = Files.newInputStream(Paths.get(sslKeyStore));
        }

        if (keyStoreStream == null) {
            throw new IllegalArgumentException("Invalid keystore configuration");
        }

        try (InputStream is = keyStoreStream) {
            KeyStore keyStore;

            if (Utils.isNotEmpty(sslKeyStoreType)) {
                //  "JKS"、"PKCS12"(.pfx)
                keyStore = KeyStore.getInstance(sslKeyStoreType);
            } else {
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            }

            keyStore.load(is, keyStorePassword);

            return keyStore;
        }
    }
}
