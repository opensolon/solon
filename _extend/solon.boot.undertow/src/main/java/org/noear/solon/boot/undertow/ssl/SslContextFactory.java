package org.noear.solon.boot.undertow.ssl;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author noear
 * @since 1.6
 */
public class SslContextFactory {

    public final static TrustManager[] TRUST_ALL_CERTS = new X509TrustManager[]{new DummyTrustManager()};

    public static SSLContext createSslContext() throws IOException {
        String keyStoreName = System.getProperty(ServerConstants.SSL_KEYSTORE);
        String keyStoreType = System.getProperty(ServerConstants.SSL_KEYSTORE_TYPE);
        String keyStorePassword = System.getProperty(ServerConstants.SSL_KEYSTORE_PASSWORD);

        if(Utils.isEmpty(keyStoreType)){
            keyStoreType = "jks";
        }

        KeyStore keyStore = loadKeyStore(keyStoreName, keyStoreType, keyStorePassword);

        KeyManager[] keyManagers = buildKeyManagers(keyStore, keyStorePassword.toCharArray());
        TrustManager[] trustManagers = buildTrustManagers(null);

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
        } catch (NoSuchAlgorithmException | KeyManagementException exc) {
            throw new IOException("Unable to create and initialise the SSLContext", exc);
        }

        return sslContext;
    }

    private static KeyStore loadKeyStore(final String location, String type, String storePassword)
            throws IOException {
        String url = location;
        if (url.indexOf(':') == -1) {
            url = "file:" + location;
        }

        final InputStream stream = new URL(url).openStream();
        try {
            KeyStore loadedKeystore = KeyStore.getInstance(type);
            loadedKeystore.load(stream, storePassword.toCharArray());
            return loadedKeystore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException exc) {
            throw new IOException(String.format("Unable to load KeyStore %s", location), exc);
        } finally {
            stream.close();
        }
    }

    private static TrustManager[] buildTrustManagers(final KeyStore trustStore) throws IOException {
        TrustManager[] trustManagers = null;
        if (trustStore == null) {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                trustManagers = trustManagerFactory.getTrustManagers();
            } catch (NoSuchAlgorithmException | KeyStoreException exc) {
                throw new IOException("Unable to initialise TrustManager[]", exc);
            }
        } else {
            trustManagers = TRUST_ALL_CERTS;
        }
        return trustManagers;
    }

    private static KeyManager[] buildKeyManagers(final KeyStore keyStore, char[] storePassword)
            throws IOException {
        KeyManager[] keyManagers;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                    .getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, storePassword);
            keyManagers = keyManagerFactory.getKeyManagers();
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException exc) {
            throw new IOException("Unable to initialise KeyManager[]", exc);
        }
        return keyManagers;
    }
}
