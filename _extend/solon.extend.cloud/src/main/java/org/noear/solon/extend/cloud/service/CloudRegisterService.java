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
     * 注册
     * */
    void put(Node instance);
    /**
     * 取消注册
     * */
    void remove(Node instance);

    /**
     * 获取
     * */
    Discovery get(String service);
}
