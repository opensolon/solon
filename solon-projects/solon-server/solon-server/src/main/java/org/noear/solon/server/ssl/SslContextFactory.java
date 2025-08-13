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
package org.noear.solon.server.ssl;

import org.noear.solon.Utils;
import org.noear.solon.server.prop.ServerSslProps;
import org.noear.solon.core.util.ResourceUtil;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author noear
 * @since 1.6
 */
public class SslContextFactory {
    public static SSLContext create(ServerSslProps serverSslProps) throws IOException {
        String sslKeyStore = serverSslProps.getSslKeyStore();
        String sslKeyType = serverSslProps.getSslKeyType();
        String sslKeyPassword = serverSslProps.getSslKeyPassword();

        if(Utils.isEmpty(sslKeyType)){
            sslKeyType = "jks";
        }

        KeyStore keyStore = loadKeyStore(sslKeyStore, sslKeyType, sslKeyPassword);

        KeyManager[] keyManagers = buildKeyManagers(keyStore, sslKeyPassword.toCharArray());

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException exc) {
            throw new IOException("Unable to create and initialise the SSLContext", exc);
        }

        return sslContext;
    }

    private static KeyStore loadKeyStore(final String location, String type, String storePassword)
            throws IOException {

        URL KeyStoreUrl = ResourceUtil.findResource(location);

        if (KeyStoreUrl == null) {
            throw new IllegalStateException("The keyStore file does not exist: " + location);
        }

        try (InputStream stream = KeyStoreUrl.openStream()) {
            KeyStore loadedKeystore = KeyStore.getInstance(type);
            loadedKeystore.load(stream, storePassword.toCharArray());
            return loadedKeystore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException exc) {
            throw new IOException(String.format("Unable to load KeyStore %s", location), exc);
        }
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
