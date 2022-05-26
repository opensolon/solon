package org.noear.solon.cloud.service;


import org.noear.solon.logging.event.LogEvent;

/**
 * 云端日志服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLogService {
    /**
     * 添加
     *
     * @param logEvent 日志事件
     * */
    void append(LogEvent logEvent);
}
