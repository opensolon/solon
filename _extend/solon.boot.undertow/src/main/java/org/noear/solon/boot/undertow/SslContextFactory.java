package org.noear.solon.boot.undertow;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
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
        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(getKeyManagers(), null, null);

        return sslContext;
    }

    protected static KeyManager[] getKeyManagers() {
        String sslKeyStore = System.getProperty(ServerConstants.SSL_KEYSTORE);
        String sslKeyStoreType = System.getProperty(ServerConstants.SSL_KEYSTORE_TYPE);
        String sslKeyStorePassword = System.getProperty(ServerConstants.SSL_KEYSTORE_PASSWORD);

        try {
            KeyStore keyStore = loadKeyStore(sslKeyStore, sslKeyStoreType, sslKeyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            char[] keyPassword = null;
            if (Utils.isNotEmpty(sslKeyStorePassword)) {
                keyPassword = sslKeyStorePassword.toCharArray();
            }

            keyManagerFactory.init(keyStore, keyPassword);

            return keyManagerFactory.getKeyManagers();

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected static KeyStore loadKeyStore(String sslKeyStore, String sslKeyStoreType, String sslKeyStorePassword) throws Exception {
        InputStream keyStoreStream = Utils.getResource(sslKeyStore).openStream();

        if (keyStoreStream == null) {
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

            if (Utils.isNotEmpty(sslKeyStorePassword)) {
                keyStore.load(is, sslKeyStorePassword.trim().toCharArray());
            } else {
                keyStore.load(is, null);
            }

            return keyStore;
        }
    }
}
