package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Config;

/**
 * 云配置处理
 *
 * @author noear
 * @since 1.2
 */
@FunctionalInterface
public interface CloudConfigHandler {
    void handle(Config config);
}
