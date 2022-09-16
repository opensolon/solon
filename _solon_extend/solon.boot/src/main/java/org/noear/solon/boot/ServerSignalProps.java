package org.noear.solon.boot;

/**
 * 服务信号属性
 *
 * @author noear
 * @since 1.8
 */
public interface ServerSignalProps {
    /**
     * 名称
     * */
    String getName();

    /**
     * 主机
     * */
    String getHost();

    /**
     * 端口
     * */
    int getPort();
}
