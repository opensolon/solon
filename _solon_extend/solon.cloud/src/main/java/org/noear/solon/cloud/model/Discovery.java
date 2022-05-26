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
    private final String service;
    private final List<Instance> cluster;
    private String agent;
    private String policy;

    public Discovery(String service) {
        this.service = service;
        this.cluster = new ArrayList<>();
    }


    /**
     * 获取服务名
     */
    public String service() {
        return service;
    }

    /**
     * 获取代理
     */
    public String agent() {
        return agent;
    }

    /**
     * 设置代理
     */
    public Discovery agent(String agent) {
        this.agent = agent;
        return this;
    }

    /**
     * 获取策略
     */
    public String policy() {
        return policy;
    }

    /**
     * 设置策略
     * */
    public Discovery policy(String policy) {
        this.policy = policy;
        return this;
    }

    /**
     * 获取集群
     * */
    public List<Instance> cluster() {
        return cluster;
    }

    /**
     * 获取集群数量
     * */
    public int clusterSize() {
        return cluster.size();
    }

    /**
     * 添加集群实例节点
     * */
    public Discovery instanceAdd(Instance instance){
        cluster.add(instance);
        return this;
    }

    /**
     * 获取集群实例节点
     * */
    public Instance instanceGet(int index) {
        return cluster.get(index % cluster.size());
    }

}
