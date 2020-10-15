package org.noear.solon.core;

/**
 * 负载器
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface XUpstream {

    /**
     * 获取节点
     * */
    String getServer();

    /**
     * 负载器工厂
     * */
    interface Factory{
        XUpstream create(String service);
    }
}
