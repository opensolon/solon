package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.handle.Context;

/**
 * IP_Hash 负载策略
 *
 * @author noear
 * @since 2.2
 */
public class CloudLoadStrategyIpHash implements CloudLoadStrategy{
    @Override
    public String getServer(Discovery discovery) {
        String ip = Context.current().realIp();
        Instance instance = discovery.instanceGet(ip.hashCode());

        return instance.uri();
    }
}
