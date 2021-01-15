package org.noear.solon.extend.cloud.model;

/**
 * 服务实例
 *
 * @author noear
 * @since 1.2
 */
public class Node {
    /**
     * 服务
     */
    public String service;
    /**
     * IP
     */
    public String ip;
    /**
     * 端口
     */
    public int port;
    /**
     * 协议（http, ws, tcp...）
     * */
    public String protocol;

    /**
     * 权重
     * */
    public double weight = 1.0D;
}
