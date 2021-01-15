package org.noear.solon.extend.cloud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现
 *
 * @author noear
 * @since 1.2
 */
public class Discovery {
    /**
     * 服务名
     */
    public String service;
    /**
     * 集群
     */
    public List<Node> cluster;

    public Discovery() {

    }

    public Discovery(String service) {
        this.service = service;
        this.cluster = new ArrayList<>();
    }
}
