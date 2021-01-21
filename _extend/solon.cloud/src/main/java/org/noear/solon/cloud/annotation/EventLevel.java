package org.noear.solon.cloud.annotation;

/**
 * @author noear 2021/1/21 created
 */
public enum EventLevel {
    /**
     * 实例级（以实例ip:port订阅）
     * */
    instance,
    /**
     * 集群级（以hostname形式订阅）
     * */
    cluster,
}
