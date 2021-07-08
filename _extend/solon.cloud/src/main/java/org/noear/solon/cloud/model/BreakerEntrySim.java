package org.noear.solon.cloud.model;

/**
 * 断路器入口模型
 *
 * @author noear
 * @since 1.2
 */
public abstract class BreakerEntrySim implements BreakerEntry{
    /**
     * 重置阀值
     * */
    public abstract void reset(int value);
}
