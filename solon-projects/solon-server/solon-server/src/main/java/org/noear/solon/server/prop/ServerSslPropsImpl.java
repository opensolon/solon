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
package org.noear.solon.server.prop;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;

/**
 * 服务证属性实现
 *
 * @author noear
 * @since 2.3
 * @since 3.5
 */
class ServerSslPropsImpl implements ServerSslProps {
    private String PROP_SSL_ENABLE = "server.@@.ssl.enable";
    private String PROP_SSL_KEY_TYPE = "server.@@.ssl.keyType";//old

    private String PROP_SSL_KEY_STORE_TYPE = "server.@@.ssl.keyStoreType"; //new v3.7.4
    private String PROP_SSL_KEY_STORE = "server.@@.ssl.keyStore";
    private String PROP_SSL_KEY_PASSWORD = "server.@@.ssl.keyPassword";

    private String PROP_SSL_TRUST_STORE_TYPE = "server.@@.ssl.trustStoreType"; //new v3.7.4
    private String PROP_SSL_TRUST_STORE = "server.@@.ssl.trustStore";
    private String PROP_SSL_TRUST_PASSWORD = "server.@@.ssl.trustPassword";

    private boolean enable;

    private String sslKeyStore;
    private String sslKeyStoreType;
    private String sslKeyPassword;

    private String sslTrustStore;
    private String sslTrustStoreType;
    private String sslTrustPassword;

    public ServerSslPropsImpl(String signalName) {
        PROP_SSL_ENABLE = PROP_SSL_ENABLE.replace("@@", signalName);
        PROP_SSL_KEY_STORE = PROP_SSL_KEY_STORE.replace("@@", signalName);

        enable = Solon.cfg().getBool(PROP_SSL_ENABLE, true);
        sslKeyStore = Solon.cfg().getByKeys(PROP_SSL_KEY_STORE, ServerConstants.SERVER_SSL_KEY_STORE);

        if (Utils.isNotEmpty(sslKeyStore)) {
            PROP_SSL_KEY_TYPE = PROP_SSL_KEY_TYPE.replace("@@", signalName);
            PROP_SSL_KEY_STORE_TYPE = PROP_SSL_KEY_STORE_TYPE.replace("@@", signalName);
            PROP_SSL_KEY_PASSWORD = PROP_SSL_KEY_PASSWORD.replace("@@", signalName);

            sslKeyStoreType = Solon.cfg().getByKeys(
                    PROP_SSL_KEY_STORE_TYPE, ServerConstants.SERVER_SSL_KEY_STORE_TYPE,
                    PROP_SSL_KEY_TYPE, ServerConstants.SERVER_SSL_KEY_TYPE);
            sslKeyPassword = Solon.cfg().getByKeys(
                    PROP_SSL_KEY_PASSWORD, ServerConstants.SERVER_SSL_KEY_PASSWORD);

            //-----------------

            PROP_SSL_TRUST_STORE = PROP_SSL_TRUST_STORE.replace("@@", signalName);

            sslTrustStore = Solon.cfg().getByKeys(
                    PROP_SSL_TRUST_STORE, ServerConstants.SERVER_SSL_TRUST_STORE);
            if (Utils.isNotEmpty(sslTrustStore)) {
                PROP_SSL_TRUST_STORE_TYPE = PROP_SSL_TRUST_STORE_TYPE.replace("@@", signalName);
                PROP_SSL_TRUST_PASSWORD = PROP_SSL_TRUST_PASSWORD.replace("@@", signalName);

                sslTrustStoreType = Solon.cfg().getByKeys(
                        PROP_SSL_TRUST_STORE_TYPE, ServerConstants.SERVER_SSL_TRUST_STORE_TYPE);
                sslTrustPassword = Solon.cfg().getByKeys(
                        PROP_SSL_TRUST_PASSWORD, ServerConstants.SERVER_SSL_TRUST_PASSWORD);
            }
        }
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public String getSslKeyStoreType() {
        return sslKeyStoreType;
    }

    @Override
    public String getSslKeyStore() {
        return sslKeyStore;
    }

    @Override
    public String getSslKeyPassword() {
        return sslKeyPassword;
    }

    @Override
    public String getSslTrustStore() {
        return sslTrustStore;
    }

    @Override
    public String getSslTrustStoreType() {
        return sslTrustStoreType;
    }

    @Override
    public String getSslTrustStorePassword() {
        return sslTrustPassword;
    }
}