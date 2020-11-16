package org.noear.fairy;

/**
 * 上游
 * */
@FunctionalInterface
public interface UpstreamService {

    /**
     * 获取节点
     * */
    String getServer();
}