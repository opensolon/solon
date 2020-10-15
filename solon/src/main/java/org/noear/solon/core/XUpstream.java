package org.noear.solon.core;

/**
 * 负载器
 * */
@FunctionalInterface
public interface XUpstream {

    /**
     * 获取节点
     * */
    String getServer();

    interface Factory{
        XUpstream create(String service);
    }
}
