package org.noear.solon.cloud.annotation;

/**
 * 云端事件级别
 *
 * @author noear
 * @since 1.2
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
