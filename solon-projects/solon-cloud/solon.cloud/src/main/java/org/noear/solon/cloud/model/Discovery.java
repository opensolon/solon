/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private transient Object attachment;
    /*
     * 附件（一般给策略使用）
     * */
    public <T> T attachment() {
        return (T) attachment;
    }

    public <T> void attachmentSet(T val){
        attachment = val;
    }


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


    @Override
    public String toString() {
        return "Discovery{" +
                "service='" + service + '\'' +
                ", policy='" + policy + '\'' +
                ", agent='" + agent + '\'' +
                ", cluster=" + cluster +
                '}';
    }
}
