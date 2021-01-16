package org.noear.solon.cloud.service;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;

import java.util.function.Consumer;

/**
 * 注册与发现服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudDiscoveryService {
    /**
     * 注册服务实例
     */
    void register(Node instance);

    /**
     * 注销服务实例
     */
    void deregister(Node instance);

    /**
     * 查询服务列表
     */
    Discovery find(String service);

    /**
     * 关注服务列表
     */
    void attention(String service, Consumer<Discovery> observer);
}
