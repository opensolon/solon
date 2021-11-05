package org.noear.solon.cloud.extend.cloudeventplus;

/**
 * @author 颖
 * @since 1.5
 */
public interface CloudEventEntityHandler<T extends CloudEventEntity> {

    /**
     * 处理
     *
     * @param event 事件
     * */
    boolean handler(T event) throws Throwable;
}
