package org.noear.solon.cloud.service;

/**
 * 云端链路跟踪服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudTraceService {
    /**
     * TraceId 头名称
     */
    String HEADER_TRACE_ID_NAME();

    /**
     * FromId 头名称
     * */
    String HEADER_FROM_ID_NAME();

    /**
     * 设置当前线程的跟踪标识
     * */
    void setLocalTraceId(String traceId);

    /**
     * 获取跟踪标识
     */
    String getTraceId();

    /**
     * 获取来源标识（service@address:port）
     * */
    String getFromId();

}
