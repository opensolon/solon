package org.noear.solon.boot.prop;

/**
 * @author noear
 * @since 2.3
 */
public interface ServerSslProps {
    static ServerSslProps of(String signalName){
        return new ServerSslPropsImpl(signalName);
    }

    String getSslKeyStore();
    String getSslKeyType();
    String getSslKeyPassword();
}
