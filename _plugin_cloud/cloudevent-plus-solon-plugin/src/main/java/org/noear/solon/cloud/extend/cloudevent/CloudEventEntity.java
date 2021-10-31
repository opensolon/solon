package org.noear.solon.cloud.extend.cloudevent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 颖
 */
public interface CloudEventEntity {

    AtomicReference<Boolean> SUCCESS = new AtomicReference<>(true);

    /**
     * 判断事件是否执行成功
     * @return 是否成功
     */
    default Boolean isSuccess() {
        return SUCCESS.get();
    }

    /**
     * 设置事件是否执行成功
     * @param success 是否成功
     */
    default void setSuccess(boolean success) {
        SUCCESS.set(success);
    }

}
