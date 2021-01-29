package org.noear.solon.cloud.extend.consul.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.service.CloudTraceService;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2021/1/29 created
 */
public class CloudTraceServiceImp implements CloudTraceService {
    static final String http_header_trace = "Solon-Trace-Id";

    @Override
    public String HEADER_TRACE_ID_NAME() {
        return http_header_trace;
    }

    @Override
    public String getTraceId() {
        Context ctx = Context.current();

        if (ctx == null) {
            return "";
        } else {
            String trace_id = ctx.header(http_header_trace);

            if (Utils.isEmpty(trace_id)) {
                trace_id = Utils.guid();
                ctx.headerMap().put(http_header_trace, trace_id);
            }

            return trace_id;
        }
    }
}
