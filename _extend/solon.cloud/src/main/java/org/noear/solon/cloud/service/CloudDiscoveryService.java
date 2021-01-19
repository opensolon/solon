package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;

/**
 * 云端注册与发现服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudDiscoveryService {
    /**
     * 注册服务实例
     */
    void register(String group, Node instance);

    /**
     * 注销服务实例
     */
    void deregister(String group, Node instance);

    /**
     * 查询服务列表
     */
    Discovery find(String group, String service);

    /**
     * 关注服务列表
     */
    void attention(String group, String service, CloudDiscoveryHandler observer);
}
