package org.noear.solon.boot;

/**
 * 服务生命周期
 *
 * @author noear
 * @since 1.8
 */
public interface ServerLifecycle {
    /**
     * 启动
     *
     * @param host 主机（ip）
     * @param port 端口
     * */
    void start(String host, int port) throws Throwable;

    /**
     * 停止
     * */
    void stop() throws Throwable;
}
