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


    public String getService() {
        return service;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public List<Instance> getCluster() {
        return cluster;
    }

    public int getClusterSize() {
        return cluster.size();
    }

    public void addInstance(Instance instance){
        cluster.add(instance);
    }

    public Instance getInstance(int index) {
        return cluster.get(index % cluster.size());
    }


    public Discovery(String service) {
        this.service = service;
        this.cluster = new ArrayList<>();
    }
}
