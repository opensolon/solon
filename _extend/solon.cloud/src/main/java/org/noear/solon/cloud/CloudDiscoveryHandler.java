package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Discovery;

/**
 * 云发现处理
 *
 * @author noear
 * @since 1.2
 */
public interface CloudDiscoveryHandler {
    void handle(Discovery discovery);
}
