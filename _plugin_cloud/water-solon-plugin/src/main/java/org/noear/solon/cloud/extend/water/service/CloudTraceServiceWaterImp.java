package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.impl.CloudTraceServiceImpl;
import org.noear.water.WW;

/**
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
