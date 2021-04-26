package org.noear.solon.cloud.model;

/**
 * @author noear 2021/4/26 created
 */
public abstract class BreakerEntrySim implements BreakerEntry{
    /**
     * 重置阀值
     * */
    public abstract void reset(int value);
}
