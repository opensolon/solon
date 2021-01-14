package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.Discovery;

/**
 * 云发现处理
 *
 * @author noear 2021/1/14 created
 */
@FunctionalInterface
public interface CloudDiscoveryHandler {
    void handler(Discovery discovery);
}
