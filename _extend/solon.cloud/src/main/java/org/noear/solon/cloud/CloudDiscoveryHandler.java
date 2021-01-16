package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Discovery;

/**
 * @author noear 2021/1/16 created
 */
public interface CloudDiscoveryHandler {
    void handler(Discovery discovery);
}
