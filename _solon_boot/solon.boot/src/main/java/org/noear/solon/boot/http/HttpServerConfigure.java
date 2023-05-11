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
     */
    void allowSsl(boolean allowSsl);

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    void addHttpPort(int port);
}
