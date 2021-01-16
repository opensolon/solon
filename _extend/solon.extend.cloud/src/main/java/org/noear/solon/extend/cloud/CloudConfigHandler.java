package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.Config;

/**
 * 云配置处理
 *
 * @author noear
 * @since 1.2
 */
@FunctionalInterface
public interface CloudConfigHandler {
    void handler(Config config);
}
