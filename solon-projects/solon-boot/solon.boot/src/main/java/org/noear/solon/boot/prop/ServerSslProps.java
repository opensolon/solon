package org.noear.solon.boot.prop;

/**
 * @author noear
 * @since 2.3
 */
public interface ServerSslProps {
    static ServerSslProps of(String signalName){
        return new ServerSslPropsImpl(signalName);
    }

    /**
     * 是否启用
     * */
    boolean isEnable();

    /**
     * Ssl 密钥文件
     * */
    String getSslKeyStore();
    /**
     * Ssl 密钥类型
     * */
    String getSslKeyType();
    /**
     * Ssl 密钥密码
     * */
    String getSslKeyPassword();
}
