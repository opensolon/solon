package org.noear.solon.boot.http;

/**
 * Http 服务器编程配置
 *
 * @author noear
 * @since 2.2
 */
public interface HttpServerConfigure {
    /**
     * 是否允许Ssl
     * @deprecated 2.3
     */
    @Deprecated
    default void allowSsl(boolean enable){
        enableSsl(enable);
    }

    /**
     * 是否支持 http2
     */
    default boolean isSupportedHttp2(){
        return false;
    }

    /**
     * 启用 http2 （不一定所有服务都支持）
     */
    default void enableHttp2(boolean enable){

    }

    /**
     * 启用 ssl
     */
    void enableSsl(boolean enable);

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    void addHttpPort(int port);
}
