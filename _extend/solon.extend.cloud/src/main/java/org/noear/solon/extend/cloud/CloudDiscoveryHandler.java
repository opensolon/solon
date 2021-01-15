package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.Discovery;

/**
 * 云发现处理
 *
 * @author noear
 * @since 1.2
 */
@FunctionalInterface
public interface CloudDiscoveryHandler {
    void handler(Discovery discovery);
}
