package org.noear.solon.cloud.service;

/**
 * 云端链路跟踪服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudTraceService {
    /**
     * 跟踪ID名
     * */
    String traceIdName();

    /**
     * 跟踪ID值
     * */
    String traceId();
}
