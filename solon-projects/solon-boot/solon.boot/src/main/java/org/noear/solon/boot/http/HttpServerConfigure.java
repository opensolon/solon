package org.noear.solon.boot.http;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;

/**
 * Http 服务器编程配置
 *
 * @author noear
 * @since 2.2
 */
public interface HttpServerConfigure {
    /**
     * 是否允许Ssl
     *
     * @deprecated 2.3
     * @removal true
     */
    @Deprecated
    default void allowSsl(boolean enable) {
        enableSsl(enable);
    }

    /**
     * 是否支持 http2
     */
    default boolean isSupportedHttp2() {
        return false;
    }

    /**
     * 启用 http2 （不一定所有服务都支持）
     */
    default void enableHttp2(boolean enable) {

    }

    /**
     * 启用 ssl
     */
    default void enableSsl(boolean enable){
        enableSsl(enable, null);
    }

    /**
     * 启用 ssl（并指定 sslContext）
     */
    void enableSsl(boolean enable, SSLContext sslContext);

    /**
     * 启用调试模式 （不一定所有服务都支持）
     */
    default void enableDebug(boolean enable) {

    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    void addHttpPort(int port);

    /**
     * 设置执行器（线程池）
     */
    void setExecutor(Executor executor);
}
