package org.noear.solon.cloud.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.service.CloudTraceService;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.3
 */
public class CloudTraceServiceImpl implements CloudTraceService {
    @Override
    public String HEADER_TRACE_ID_NAME() {
        return "X-Solon-Trace-Id";
    }

    @Override
    public String HEADER_FROM_ID_NAME() {
        return "X-Solon-From-Id";
    }

    @Override
    public String getTraceId() {
        Context ctx = Context.current();

        if (ctx == null) {
            return "";
        } else {
            String trace_id = ctx.header(HEADER_TRACE_ID_NAME());

            if (Utils.isEmpty(trace_id)) {
                trace_id = Utils.guid();
                ctx.headerMap().put(HEADER_TRACE_ID_NAME(), trace_id);
            }

            return trace_id;
        }
    }

    @Override
    public String getFromId() {
        Context ctx = Context.current();

        if (ctx == null) {
            return "";
        } else {
            String from_id = ctx.header(HEADER_FROM_ID_NAME());
            if (Utils.isEmpty(from_id)) {
                from_id = ctx.realIp();
                ctx.headerMap().put(HEADER_FROM_ID_NAME(), from_id);
            }

            return from_id;
        }
    }
}
