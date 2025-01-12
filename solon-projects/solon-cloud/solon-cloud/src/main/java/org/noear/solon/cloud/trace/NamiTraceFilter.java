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
package org.noear.solon.cloud.trace;

import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.NamiManager;
import org.noear.nami.Result;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Instance;

/**
 * Nami 跟踪过滤器
 *
 * @author noear
 * @since 1.5
 */
public class NamiTraceFilter implements Filter {
    /**
     * 注册过滤器
     * */
    public static void register() {
        NamiManager.reg(new NamiTraceFilter());
    }

    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        inv.headers.put(CloudClient.trace().HEADER_TRACE_ID_NAME(), CloudClient.trace().getTraceId());
        inv.headers.put(CloudClient.trace().HEADER_FROM_ID_NAME(), Instance.local().serviceAndAddress());
        return inv.invoke();
    }
}
