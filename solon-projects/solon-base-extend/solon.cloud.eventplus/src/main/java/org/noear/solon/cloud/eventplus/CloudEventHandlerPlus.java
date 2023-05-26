package org.noear.solon.cloud.eventplus;

/**
 * 云端事件扩展代理
 *
 * @author 颖
 * @since 1.5
 */
public interface CloudEventHandlerPlus<T extends CloudEventEntity> {

    /**
     * 处理
     *
     * @param event 事件
     * */
    boolean handle(T event) throws Throwable;
}
