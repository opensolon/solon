package org.noear.solon.cloud.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

/**
 * 负载均衡
 *
 * @author noear
 * @since 1.2
 */
public class CloudLoadBalance implements LoadBalance {
    private String service;
    private String group;
    private Discovery discovery;
    private LoadBalance loadBalance;

    private int index = 0;
    private static final int indexMax = 99999999;

    public CloudLoadBalance(String group, String service) {
        this.service = service;
        this.group = group;

        if (CloudClient.discovery() != null) {
            discovery = CloudClient.discovery().find(group, service);

            CloudClient.discovery().attention(group, service, d1 -> {
                discovery = d1;
            });
        }
    }

    public CloudLoadBalance(String group, String service, Discovery discovery) {
        this.service = service;
        this.group = group;
        this.discovery = discovery;
    }

    public CloudLoadBalance(String group, String service, LoadBalance loadBalance) {
        this.service = service;
        this.group = group;
        this.loadBalance = loadBalance;
    }

    /**
     * 服务组
     */
    public String getGroup() {
        return group;
    }

    /**
     * 服务名
     */
    public String getService() {
        return service;
    }

    /**
     * 服务发现数据
     */
    public Discovery getDiscovery() {
        return discovery;
    }

    @Override
    public String getServer() {
        if (loadBalance != null) {
            return loadBalance.getServer();
        }

        if (discovery == null) {
            return null;
        } else {
            if (Utils.isNotEmpty(discovery.agent())) {
                return discovery.agent();
            } else {
                int count = discovery.clusterSize();

                if (count == 0) {
                    return null;
                } else {
                    //这里不需要原子性，快就好
                    if (index > indexMax) {
                        index = 0;
                    }

                    Instance instance = discovery.instanceGet(index++ % count);

                    return instance.uri();
                }
            }
        }
    }
}
