package org.noear.solon.net.http.impl;

/**
 * Http 超时：单位：秒
 *
 * @author noear
 * @since 1.7
 */
public class HttpTimeout {
    /**
     * 连接超时
     */
    public final int connectTimeout;
    /**
     * 写超时
     */
    public final int writeTimeout;
    /**
     * 读超时
     */
    public final int readTimeout;

    public HttpTimeout(int timeout) {
        this.connectTimeout = timeout;
        this.writeTimeout = timeout;
        this.readTimeout = timeout;
    }

    public HttpTimeout(int connectTimeout, int writeTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
    }
}
