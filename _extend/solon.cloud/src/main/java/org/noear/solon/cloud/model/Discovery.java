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
    public String service;

    /**
     * 代理
     * */
    public String agent;

    /**
     * 策略
     * */
    public String policy;

    /**
     * 集群
     */
    public List<Instance> cluster;

    public Discovery() {

    }

    public Discovery(String service) {
        this.service = service;
        this.cluster = new ArrayList<>();
    }
}
