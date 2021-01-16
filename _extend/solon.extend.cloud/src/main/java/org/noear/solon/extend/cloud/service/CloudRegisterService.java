package org.noear.solon.extend.cloud.service;

import org.noear.solon.extend.cloud.model.Discovery;
import org.noear.solon.extend.cloud.model.Node;

import java.util.function.BiConsumer;

/**
 * 注册服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudRegisterService {
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
    Discovery find(String group, String service);

    /**
     * 关注服务列表
     */
    void attention(String group, String service, BiConsumer<String, Discovery> observer);
}
