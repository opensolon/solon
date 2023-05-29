package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

/**
 * 云端注册与发现服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudDiscoveryService {
    /**
     * 注册服务实例
     *
     * @param group 分组
     * @param instance 服务实例
     */
    void register(String group, Instance instance);

    /**
     * 注册服务实例健康状态
     *
     * @param group 分组
     * @param instance 服务实例
     * @param health 是否健康
     */
    void registerState(String group, Instance instance, boolean health);

    /**
     * 注销服务实例
     *
     * @param group 分组
     * @param instance 服务实例
     */
    void deregister(String group, Instance instance);

    /**
     * 查询服务实例列表
     *
     * @param group 分组
     * @param service 服各名
     */
    Discovery find(String group, String service);

    /**
     * 关注服务实例列表
     *
     * @param group 分组
     * @param service 服各名
     * @param observer 观察者
     */
    void attention(String group, String service, CloudDiscoveryHandler observer);
}
