package org.noear.solon.boot.prop;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

/**
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

        sslKeyStore = Solon.cfg().getOr(PROP_SSL_KEY_STORE, ServerConstants.SERVER_KEY_STORE);
        enable = Solon.cfg().getBool(PROP_SSL_ENABLE, true);

        if (Utils.isNotEmpty(sslKeyStore)) {
            PROP_SSL_KEY_PASSWORK = PROP_SSL_KEY_PASSWORK.replace("@@", signalName);
            PROP_SSL_KEY_TYPE = PROP_SSL_KEY_TYPE.replace("@@", signalName);

            sslKeyType = Solon.cfg().getOr(PROP_SSL_KEY_TYPE, ServerConstants.SERVER_KEY_TYPE);
            sslKeyPassword = Solon.cfg().getOr(PROP_SSL_KEY_PASSWORK, ServerConstants.SERVER_KEY_PASSWORD);
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
