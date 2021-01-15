package org.noear.solon.extend.cloud.service;

import org.noear.solon.extend.cloud.model.Discovery;
import org.noear.solon.extend.cloud.model.Node;

/**
 * 注册服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudRegisterService {
    /**
     * 注册服务实例
     * */
    void register(Node instance);
    /**
     * 注销服务实例
     * */
    void deregister(Node instance);

    /**
     * 查询服务
     * */
    Discovery find(String service);
}
