package org.noear.solon.cloud.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 发现模型
 *
 * @author noear
 * @since 1.2
 */
public class Discovery implements Serializable {
    /**
     * 服务名
     */
    private final String service;

    /**
     * 集群
     */
    private final List<Instance> cluster;

    /**
     * 代理
     */
    private String agent;

    /**
     * 策略
     */
    private String policy;


    public String service() {
        return service;
    }

    public String agent() {
        return agent;
    }

    public Discovery agent(String agent) {
        this.agent = agent;
        return this;
    }

    public String policy() {
        return policy;
    }

    public Discovery policy(String policy) {
        this.policy = policy;
        return this;
    }

    public List<Instance> cluster() {
        return cluster;
    }

    public int clusterSize() {
        return cluster.size();
    }

    public Discovery instanceAdd(Instance instance){
        cluster.add(instance);
        return this;
    }

    public Instance instanceGet(int index) {
        return cluster.get(index % cluster.size());
    }


    public Discovery(String service) {
        this.service = service;
        this.cluster = new ArrayList<>();
    }
}
