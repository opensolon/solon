/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.service.CloudTraceService;
import org.noear.solon.core.FactoryManager;
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

    private final ThreadLocal<String> traceIdLocal = FactoryManager.getGlobal().newThreadLocal(CloudTraceServiceImpl.class, false);

    @Override
    public void setLocalTraceId(String traceId) {
        traceIdLocal.set(traceId);
    }

    @Override
    public String getTraceId() {
        Context ctx = Context.current();

        if (ctx == null) {
            String traceId = traceIdLocal.get();
            if (Utils.isEmpty(traceId)) {
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
