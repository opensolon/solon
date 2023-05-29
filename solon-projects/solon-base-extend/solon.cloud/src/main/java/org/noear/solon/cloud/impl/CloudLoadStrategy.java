package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;

/**
 * 负载策略
 *
 * @author noear
 * @since 2.2
 */
public interface CloudLoadStrategy {
    String getServer(Discovery discovery);
}
