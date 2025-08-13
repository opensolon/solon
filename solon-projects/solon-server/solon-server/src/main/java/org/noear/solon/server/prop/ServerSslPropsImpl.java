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
 */
class ServerSslPropsImpl implements ServerSslProps{
    private String PROP_SSL_ENABLE     = "server.@@.ssl.enable";
    private String PROP_SSL_KEY_TYPE     = "server.@@.ssl.keyType";
    private String PROP_SSL_KEY_STORE    = "server.@@.ssl.keyStore";
    private String PROP_SSL_KEY_PASSWORK = "server.@@.ssl.keyPassword";

    private boolean enable;
    private String sslKeyType;
    private String sslKeyStore;
    private String sslKeyPassword;

    public ServerSslPropsImpl(String signalName) {
        PROP_SSL_KEY_STORE = PROP_SSL_KEY_STORE.replace("@@", signalName);
        PROP_SSL_ENABLE = PROP_SSL_ENABLE.replace("@@", signalName);

        sslKeyStore = Solon.cfg().getByKeys(PROP_SSL_KEY_STORE, ServerConstants.SERVER_SSL_KEY_STORE);
        enable = Solon.cfg().getBool(PROP_SSL_ENABLE, true);

        if (Utils.isNotEmpty(sslKeyStore)) {
            PROP_SSL_KEY_PASSWORK = PROP_SSL_KEY_PASSWORK.replace("@@", signalName);
            PROP_SSL_KEY_TYPE = PROP_SSL_KEY_TYPE.replace("@@", signalName);

            sslKeyType = Solon.cfg().getByKeys(PROP_SSL_KEY_TYPE, ServerConstants.SERVER_SSL_KEY_TYPE);
            sslKeyPassword = Solon.cfg().getByKeys(PROP_SSL_KEY_PASSWORK, ServerConstants.SERVER_SSL_KEY_PASSWORD);
        }
    }


    @Override
    public String getSslKeyType() {
        return sslKeyType;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public String getSslKeyStore() {
        return sslKeyStore;
    }

    @Override
    public String getSslKeyPassword() {
        return sslKeyPassword;
    }

}
