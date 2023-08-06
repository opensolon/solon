package org.noear.solon.logging;

/**
 * 日志孵化器
 *
 * @author noear
 * @since 2.4
 */
public interface LogIncubator {
    /**
     * 孵化
     * */
    void incubate() throws Throwable;
}
