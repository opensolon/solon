package org.noear.nami.channel.http.okhttp;

/**
 * 超时：单位：秒
 *
 * @author noear
 * @since 1.7
 */
public class TimeoutProps {
    public final int connectTimeout;
    public final int writeTimeout;
    public final int readTimeout;

    public TimeoutProps(int timeout) {
        this.connectTimeout = timeout;
        this.writeTimeout = timeout;
        this.readTimeout = timeout;
    }

    public TimeoutProps(int connectTimeout, int writeTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
    }
}
