package org.noear.solonclient;

/**
 * 负载器
 * */
@FunctionalInterface
public interface XUpstream {
    /**
     * 设置备份节点
     * */
    default void setBackup(String server){}

    /**
     * 获取节点
     *
     * @param service 服务名（给多服务网关预留）
     * */
    String getServer(String service);
}
