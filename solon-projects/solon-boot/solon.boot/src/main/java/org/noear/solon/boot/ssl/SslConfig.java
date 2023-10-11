package org.noear.solon.boot.ssl;

import org.noear.solon.boot.prop.ServerSslProps;

import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * @author noear
 * @since 2.5
 */
public class SslConfig {
    /**
     * 信号名（http,socket,websocket）
     */
    private String signalName;
    /**
     * Ssl 配置属性
     */
    private ServerSslProps sslProps;
    /**
     * Ssl 上下文
     */
    private SSLContext sslContext;
    /**
     * Ssl 是否启用（默认启用）
     */
    private boolean sslEnable = true;

    public SslConfig(String signalName) {
        this.signalName = signalName;
    }

    /**
     * 是否支持
     * */
    private boolean isSupported() {
        if (sslContext != null) {
            return true;
        }

        if (sslProps == null) {
            sslProps = ServerSslProps.of(signalName);
        }

        return sslProps.isEnable() && sslProps.getSslKeyStore() != null;
    }

    /**
     * 设置
     * */
    public void set(boolean enable, SSLContext sslContext) {
        this.sslEnable = enable;
        this.sslContext = sslContext;
    }

    /**
     * 获取属性（可能是 null）
     * */
    public ServerSslProps getProps() {
        return sslProps;
    }

    /**
     * 是否启用
     * */
    public boolean isSslEnable() {
        return sslEnable && isSupported();
    }

    /**
     * 获取上下文
     * */
    public SSLContext getSslContext() throws IOException {
        if (sslContext != null) {
            return sslContext;
        } else {
            return SslContextFactory.create(sslProps);
        }
    }
}
