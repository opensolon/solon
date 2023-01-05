package org.noear.solon.boot.prop;

/**
 * 服务信号属性
 *
 * @author noear
 * @since 1.8
 * @since 1.12
 */
public interface ServerSignalProps {
    /**
     * 名称
     */
    String getName();

    /**
     * 主机
     */
    String getHost();

    /**
     * 端口
     */
    int getPort();

    /**
     * 影像主机
     *
     * @since 1.12
     * */
    String getImageHost();

    /**
     * 影像端口
     *
     * @since 1.12
     * */
    int getImagePort();

}
