package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.service.CloudTraceService;
import org.noear.solon.core.handle.Context;
import org.noear.water.WW;
import org.noear.water.utils.TextUtils;

/**
 * @author noear
 * @since 1.2
 */
public class CloudTraceServiceWaterImp implements CloudTraceService {
    @Override
    public String HEADER_TRACE_ID_NAME() {
        return WW.http_header_trace;
    }

    @Override
    public String HEADER_FROM_ID_NAME() {
        return WW.http_header_from;
    }

    @Override
    public String getTraceId() {
        Context ctx = Context.current();

        if (ctx == null) {
            return "";
        } else {
            String trace_id = ctx.header(WW.http_header_trace);

            if (TextUtils.isEmpty(trace_id)) {
                trace_id = Utils.guid();
                ctx.headerMap().put(WW.http_header_trace, trace_id);
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
            }

            return from_id;
        }
    }
}
