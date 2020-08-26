package org.noear.solon.core;

/**
 * 负载器
 * */
@FunctionalInterface
public interface XUpstreamFactory {
    /**
     * 获取节点
     */
    XUpstream create(String service);
}
