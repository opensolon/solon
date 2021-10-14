package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.impl.CloudTraceServiceImpl;
import org.noear.water.WW;

/**
 * 分布式跟踪服务
 *
 * @author noear
 * @since 1.2
 */
public class CloudTraceServiceWaterImp extends CloudTraceServiceImpl {
    @Override
    public String HEADER_TRACE_ID_NAME() {
        return WW.http_header_trace;
    }

    @Override
    public String HEADER_FROM_ID_NAME() {
        return WW.http_header_from;
    }
}
