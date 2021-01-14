package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.Instance;

/**
 * 注册服务
 *
 * @author noear 2021/1/14 created
 */
public interface CloudRegisterService {
    void register(Instance instance);
}
