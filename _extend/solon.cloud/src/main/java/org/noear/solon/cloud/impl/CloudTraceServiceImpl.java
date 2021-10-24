package org.noear.solon.cloud.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.service.CloudTraceService;
import org.noear.solon.core.handle.Context;

import java.util.Map;

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

    static final ThreadLocal<String> traceIdLocal = new InheritableThreadLocal<>();

    @Override
    public void setTraceId(String traceId) {
        traceIdLocal.set(traceId);
    }

    @Override
    public String getTraceId() {
        Context ctx = Context.current();

        if (ctx == null) {
            String traceId = traceIdLocal.get();
            if (traceId == null) {
                traceId = Utils.guid();
                traceIdLocal.set(traceId);
            }

            return traceId;
        } else {
            String traceId = ctx.header(HEADER_TRACE_ID_NAME());

            if (Utils.isEmpty(traceId)) {
                traceId = Utils.guid();
                ctx.headerMap().put(HEADER_TRACE_ID_NAME(), traceId);
            }

            return traceId;
        }
    }

    @Override
    public String getFromId() {
        Context ctx = Context.current();

        if (ctx == null) {
            return "";
        } else {
            String fromId = ctx.header(HEADER_FROM_ID_NAME());
            if (Utils.isEmpty(fromId)) {
                fromId = ctx.realIp();
                ctx.headerMap().put(HEADER_FROM_ID_NAME(), fromId);
            }

            return fromId;
        }
    }
}
